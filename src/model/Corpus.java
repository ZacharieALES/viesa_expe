//Copyright (C) 2012 Zacharie ALES and Rick MORITZ
//
//This file is part of Viesa.
//
//Viesa is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Viesa is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with Viesa.  If not, see <http://www.gnu.org/licenses/>.
package model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import javax.swing.SwingWorker;

import View.StandardView;
import clustering.AbstractClusteringMethod;
import clustering.Cluster;
import clustering.ClusterSet;
import clustering.ClusteringSolution;
import clustering.HardClusteringSolution;
import exception.AbstractException;
import exception.InvalidArgumentsToCreateAnAAColumnFormat;
import exception.UndefinedColumnFormatException;
import experesults.ExperienceResults;
import experesults.ExtractionAction;
import extraction.ExtractionAlignments;
import extraction.LPCA;
import extraction.PositiveScoreTable;
import extraction.SABRE;
import main.Main;
import main.MainTutorial;
import model.AAColumnFormat.ColumnType;
import model.Alignment.ExtractionMethod;
import model.Pattern.PatternStatus;

/**
 * Contains a corpus of annotated arrays.
 * 
 * @author zach
 * 
 */
public class Corpus implements Observable, Serializable {

	// TODO Enable to select if we want to filter or not the patterns and if we
	// want to compute the similarity or not (or compute the similarity only if
	// we cluster the patterns

	private static final long serialVersionUID = -3657405496238415664L;


	public ExperienceResults results;
	/**
	 * Private constructor since Corpus is a singleton
	 */
	private Corpus() {

		// The empty annotation is always the first element in <annotations>
		annotations.add("empty annotation");
		arrays = new ArrayList<AnnotatedArray>();
	};

	/**
	 * Class used as a holder to implement the singleton design pattern (also
	 * deal with synchronization)
	 * 
	 * @author zach
	 *
	 */
	private static class CorpusHolder {

		/** Unique and non pre-initialized instance */
		private final static Corpus c = new Corpus();
	}

	public static Corpus getCorpus() {
		return CorpusHolder.c;
	}

	private List<AnnotatedArray> arrays;

	public List<Integer> initialAnnotationColumns;
	public List<Integer> initialCommentColumns;

	private List<Pattern> patterns = new ArrayList<Pattern>();

	/**
	 * Parameter true if the extraction process is completly performed
	 * If it is equal to false, the clustering step will not be performed
	 */
	private boolean extractionCompleted = false;

	public AAColumnFormat aacf;

	/*
	 * Solutions of clustering algorithm (can be hard or hierarchical)
	 */
	private List<ClusteringSolution> al_clusteringSolution = new ArrayList<ClusteringSolution>();

	/* Annotations which occurs in the corpus */
	private List<String> annotations = new ArrayList<String>();

	private transient List<CorpusObserver> listObserver = new ArrayList<CorpusObserver>();

	private double[][] patternsSimilarity;

	public enum Clustering_algorithm{
		SINGLE_LINK,
		ROCK
	}

	private long SABRETime;
	private long LPCATime;

	private void add(AnnotatedArray aa) {

		if (arrays == null) {
			arrays = new ArrayList<AnnotatedArray>();
		}

		/* If it's not the first element of the Corpus */
		if (arrays.size() != 0) {

			/* If the aa is compatible with the column format of the corpus */
			if (!aa.isCompatibleWith(aacf))
				System.err.println("The column number ("
						+ aa.getNumberOfAnnotationColumns()
						+ ") of the AnnotatedArray "
						+ aa.getFileName()
						+ " is not compatible with the first of the corpus (which is : "
						+ arrays.get(0).getFileName() + "("
						+ arrays.get(0).getNumberOfAnnotationColumns() + "))");
			else {
				arrays.add(aa);
				notifyObserverAddAA(aa);
			}
		}

		else {
			arrays.add(aa);
			notifyObserverAddAA(aa);
		}

	}

	/**
	 * Return the index associated to a given annotation
	 * 
	 * @param s
	 * @return
	 */

	public short getAnnotationIndex(String s) {
		int i = annotations.indexOf(s);
		int result;

		if (i == -1) {
			result = annotations.size();
			annotations.add(s);
		} else
			result = i;
		return (short) result;
	}

	/**
	 * Get the annotation associated to an index
	 * 
	 * @param i
	 * @return
	 */
	public String getAnnotation(int i){
		return annotations.get(i);
	}

	public List<String> getAnnotations() {
		return annotations;
	}

	/**
	 * Set the column format of the array in the corpus
	 * 
	 * @param commentColumns
	 *            Index of the comment columns in the input files (null or empty
	 *            if their is no comment column)
	 * @param annotationColumns
	 *            Index of the annotation columns in the input files (null or
	 *            empty if their is no annotation column)
	 * @param numericalColumns
	 *            Index of the numerical annotation columns in the input files
	 *            null or empty if their is no numerical annotation column)
	 * @param annotation_similarities
	 *            Similarity between annotations in annotation columns
	 * @throws InvalidArgumentsToCreateAnAAColumnFormat
	 */
	public void setColumnFormat(ArrayList<Integer> commentColumns, ArrayList<Integer> annotationColumns,
			ArrayList<Integer> numericalColumns) throws InvalidArgumentsToCreateAnAAColumnFormat {

		ArrayList<PositionedColumn> al_pc = new ArrayList<PositionedColumn>();

		if (commentColumns != null)
			for (Integer i : commentColumns)
				al_pc.add(new PositionedColumn(new CommentColumn(), i));

		if (annotationColumns != null)
			for (Integer i : annotationColumns)
				al_pc.add(new PositionedColumn(new AnnotationColumn(), i));

		aacf = new AAColumnFormat(al_pc);

	}

	/**
	 * Create AnnotatedArrays from one or several csv files (1 csv files <-> 1
	 * annotated array) and add them in the corpus.
	 * 
	 * @param sPath
	 *            Path of one csv file or path of a folder which contains csv
	 *            files
	 * @throws UndefinedAnnotationInSMException
	 */
	public void add(String sPath, boolean h_header) throws UndefinedColumnFormatException {

		if (aacf != null) {

			File directory = new File(sPath);

			/* If the file exist */
			if (!directory.exists())
				System.err.println("File/folder " + directory + " doesn't exist");
			else {

				File[] subFiles = null;

				if (!directory.isDirectory()) {
					subFiles = new File[1];
					subFiles[0] = new File(sPath);
				} else
					subFiles = directory.listFiles();

				/* Regexp to test that the end of the file end by ".csv" */
				java.util.regex.Pattern csvRegexp = java.util.regex.Pattern.compile(".*csv");

				/*
				 * For each file at <sPath>
				 */
				for (int j = 0; j < subFiles.length; j++) {

					Matcher fileName = csvRegexp.matcher(subFiles[j].getName());
					// System.out.println("file name : " +
					// subFiles[j].getName());

					/* If the file is a csv file */
					if (subFiles[j].isFile() && fileName.matches()) {

						try {
							AnnotatedArray aa = new AnnotatedArray(subFiles[j].getPath(), h_header, aacf);
							this.add(aa);
						} catch (AbstractException e) {
							notifyObserverAbstractException(e);
						}
					}
				}
			}
		}

		/* If aacf == null */
		else {
			throw new UndefinedColumnFormatException();
		}

	}

	public void remove(AnnotatedArray aa) {
		int id = arrays.indexOf(aa);
		arrays.remove(id);
		notifyObserverRemoveAA(id);

	}


	public void remove(int id) {
		arrays.remove(id);

		notifyObserverRemoveAA(id);
	}

	public void initPatternSimilarity() {
		patternsSimilarity = new double[patterns.size()][patterns.size()];
		for (int i = 0; i < patterns.size(); i++) {
			patternsSimilarity[i][i] = 0;
		}
	}

	public void calculateSimilarity(Pattern p1, Pattern p2) {
		double similarity = p1.similarity(p2);
		// int similarity = -1;

		patternsSimilarity[p1.getIndex()][p2.getIndex()] = similarity;
		patternsSimilarity[p2.getIndex()][p1.getIndex()] = similarity;

	}

	public void displaySimilarities() {

		for (int i = 1; i < patterns.size(); ++i) {
			for (int j = 0; j < i; ++j) {
				double k = (-patternsSimilarity[patterns.get(i).getIndex()][patterns.get(j).getIndex()]);
				System.out.print(k + " ");
			}
			System.out.println();
		}

	}

	public void clearPatternsAndClusteringSolutions() {
		patterns.clear();
		al_clusteringSolution.clear();

		notifyObserverClearClusters();
		notifyObserverPatterns();
	}


	public void aaAlignment(AnnotatedArray aa1, AnnotatedArray aa2, ExtractionAlignments extractedAlignments, ExtractionAlignments extractedAlignmentsLPCA) {

		/* Extract the alignments between aa1 and aa2 */


		long start = System.currentTimeMillis();
		extractedAlignments.addAlignments(SABRE.getInstance().align(aa1, aa2, extractedAlignments.getMinimumScore()));
		SABRETime += System.currentTimeMillis() - start;

		if(!MainTutorial.IS_TUTO){
			start = System.currentTimeMillis();
			extractedAlignmentsLPCA.addAlignments(LPCA.getInstance().align(aa1, aa2, extractedAlignmentsLPCA.getMinimumScore()));
			LPCATime += System.currentTimeMillis() - start;
		}
		//		System.out.println("In Corpus: number of alignments: " + SABREExtractedAlignments.getCurrentNumberOfAlignments()+ " min score: " + SABREExtractedAlignments.getMinimumScore());


		//		System.out.println("In Corpus: number of alignments: " + LPCAExtractedAlignments.getCurrentNumberOfAlignments()+ " min score: " + LPCAExtractedAlignments.getMinimumScore());

	}

	public ClusteringSolution getClusteringSolution(int i) {
		return al_clusteringSolution.get(i);
	}

	public int getClusteringSolutionSize() {
		return al_clusteringSolution.size();
	}

	public void add(ClusteringSolution cs) {
		al_clusteringSolution.add(cs);
		notifyObserverClusters(cs);
	}

	public Double safeSimilarity(Pattern p1, Pattern p2) {

		if(p1 == null || p2 == null)
			return null;

		int id1 = patterns.indexOf(p1);
		int id2 = patterns.indexOf(p2);

		if(id1 >= 0 && id2 >= 0 && patternsSimilarity != null && patternsSimilarity.length > id1 && patternsSimilarity[id1] != null && patternsSimilarity[id1].length > id2)
			return patternsSimilarity[patterns.indexOf(p1)][patterns.indexOf(p2)];
		else
			return null;

	}

	public double similarity(Pattern p1, Pattern p2) {

		return patternsSimilarity[patterns.indexOf(p1)][patterns.indexOf(p2)];
	}

	public double similarity(int p1, int p2) {

		return patternsSimilarity[p1][p2];
	}

	public void addObserver(Observer obs) {

		if (listObserver == null) {
			listObserver = new ArrayList<CorpusObserver>();
		}

		CorpusObserver cobs = (CorpusObserver)obs;
		listObserver.add(cobs);

		if(patterns != null && patterns.size() > 0)
			cobs.updatePatterns();

		if(al_clusteringSolution != null && al_clusteringSolution.size() > 0)
			for(ClusteringSolution cs: al_clusteringSolution)
				cobs.updateClusters(cs);

		cobs.updateSABREParam();

		if(AnnotationColumn.pst != null)
			cobs.updateScoreSimilarities(AnnotationColumn.pst);

		//updateMaxSimilarity(NumericalColumn.maxSim);

		if(arrays != null)
			for(AnnotatedArray aa: arrays)
				cobs.updateAddAA(aa);

	}

	public void removeAllObserver() {
		if (listObserver != null)
			listObserver.clear();
	}

	public void removeObserver() {
		listObserver = new ArrayList<CorpusObserver>();
	}

	private void notifyObserverAddAA(AnnotatedArray aa) {
		for (CorpusObserver obs : listObserver) {
			obs.updateAddAA(aa);
		}
	}

	private void notifyObserverRemoveAA(int id) {
		for (CorpusObserver obs : listObserver) {
			obs.updateRemoveAA(id);
		}
	}

	private void notifyObserverExtractionEndOfProcess() {
		for (CorpusObserver obs : listObserver) {
			obs.endOfExtractionProcess();
		}
	}

	private void notifyObserverSwingWorker(SwingWorker<?, ?> sw) {
		for (CorpusObserver obs : listObserver) {
			obs.updateSwingWorker(sw);
		}
	}

	public void notifyObserverEndOfClusteringProcess() {
		for (CorpusObserver obs : listObserver)
			obs.endOfClusteringProcess();
	}

	private void notifyObserverDesiredNumberOfAlignments(int v) {
		for (CorpusObserver obs : listObserver)
			obs.updateSABREParam();
	}

	private void notifyObserverPatterns() {
		for (CorpusObserver obs : listObserver)
			obs.updatePatterns();
	}

	private void notifyObserverRemoveClusteringSolution(int i) {
		for (CorpusObserver obs : listObserver)
			obs.removeClusteringSolution(i);
	}

	private void notifyObserverClearClusters() {
		for (CorpusObserver obs : listObserver)
			obs.clearClusters();
	}

	private void notifyObserverClusters(ClusteringSolution cs) {
		for (CorpusObserver obs : listObserver)
			obs.updateClusters(cs);
	}

	private void notifyObserverAbstractException(AbstractException e) {
		for (CorpusObserver obs : listObserver)
			obs.abstractException(e);
	}

	private void notifyObserverScoreSimilarities(PositiveScoreTable p) {
		for (CorpusObserver obs : listObserver)
			obs.updateScoreSimilarities(p);
	}

	private void notifyObserverMaxSimilarity(Double d) {
		for (CorpusObserver obs : listObserver) {
			obs.updateMaxSimilarity(d);
		}
	}

	public AnnotatedArray getAA(int i) {
		return arrays.get(i);
	}

	public int getAASize() {
		return arrays.size();
	}

	public void setDesiredNumberOfAlignments(int value) {
		SABRE.getInstance().getParam().desired_number_of_alignments = value;
		notifyObserverDesiredNumberOfAlignments(value);
	}


	public void removeClusteringSolution(int i) {
		al_clusteringSolution.remove(i);
		notifyObserverRemoveClusteringSolution(i);
	}

	public double compareToP(Pattern p, Pattern p1, Pattern p2) {
		return similarity(p, p1) - similarity(p, p2);
	}

	public double[][] getPatternsSimilarity() {
		return patternsSimilarity;
	}

	public void setAnnotationSimilarities(PositiveScoreTable p) {
		AnnotationColumn.pst = p;
		notifyObserverScoreSimilarities(p);
	}

	public void setMaxDistance(Double max) {
		//		NumericalColumn.maxSim = max;
		notifyObserverMaxSimilarity(max);
	}

	public PositiveScoreTable getAnnotationSimilarities() {
		return AnnotationColumn.pst;
	}

	public int getAnnotationColumnNb() {
		return arrays.size() == 0 ? 0 : arrays.get(0).getNumberOfAnnotationColumns();
	}

	public Pattern getPattern(int i) {
		return patterns.get(i);
	}

	public int getPatternSize() {
		return patterns.size();
	}

	public void extractPatterns() {
		executeSwingWorker(new CorpusExtraction());
	}

	public void clusterPatterns(ArrayList<AbstractClusteringMethod> clusteringToPerform) {

		if (extractionCompleted) {

			AbstractClusteringMethod.remainingClusteringMethodToProcess = clusteringToPerform.size();

			for(AbstractClusteringMethod method : clusteringToPerform)
				executeSwingWorker(method);
		}

	}

	public void executeSwingWorker(SwingWorker<Void, Void> sw) {
		notifyObserverSwingWorker(sw);
		sw.execute();
	}
	public List<AnnotatedArray> getAA() {

		return Collections.unmodifiableList(arrays);
	}

	public List<ClusteringSolution> getClusteringSolutions() {
		return Collections.unmodifiableList(al_clusteringSolution);
	}

	public List<Pattern> getPatterns() {
		return Collections.unmodifiableList(patterns);
	}

	/**
	 * Class used to extract patterns. It is a swing worker in order not to
	 * freeze the GUI while performing the compuation. The class also create a
	 * pattern partition in which a cluster contain two patterns which have been
	 * extracted together. The similarity between all pairs of patterns is
	 * computed.
	 * 
	 * @author zach
	 *
	 */
	public class CorpusExtraction extends SwingWorker<Void, Void> {

		/*
		 * This value tracks the progress of the processing, for feedback to
		 * connected Views
		 */
		private double progress;

		@Override
		protected Void doInBackground() throws Exception {

			// System.out.println("Corpus Extraction : do in background");
			firePropertyChange("description", "", "Extract patterns alignements");

			//			LPCA.getInstance().resetExtractionInsDelScore();

			if(al_clusteringSolution != null)
				while(al_clusteringSolution.size() > 0)
					removeClusteringSolution(0);


			extractionCompleted = false;
			ExtractionAlignments extractedAlignmentsSABRE = new ExtractionAlignments();
			ExtractionAlignments extractedAlignmentsLPCA = new ExtractionAlignments();

			try {

				double invMaxProgress = 1.0 / (((getAASize() - 1) * (getAASize() - 2)) >> 1);
				double step = 100.0 * invMaxProgress;

				this.resetProgress();

				Corpus.getCorpus().clearPatternsAndClusteringSolutions();

				SABRETime = 0;
				LPCATime = 0;

				int i = 0;

				/* For each couple of AnnotatedArray in the corpus */
				while (i < getAASize() && !isCancelled()) {
					for (int j = 0; j < i; j++) {
						if (!isCancelled()) {

							/* Extract Alignments */
							aaAlignment(getAA(i), getAA(j), extractedAlignmentsSABRE, extractedAlignmentsLPCA);

							progress(step);

						} else {
							this.done();
						}

					}

					i++;

				}

				patterns = extractedAlignmentsSABRE.getPatterns();

				createLPCAAndSABREClustering(extractedAlignmentsSABRE, extractedAlignmentsLPCA);
				//					initPatternSimilarity();					


				if(isCancelled()){
					this.done();
					System.out.println("Corpus: isCancelled, this.done()");
				}

			}catch(Exception e){
				e.printStackTrace();
			}

			if(!isCancelled()){
				extractionCompleted = true;

				//					System.out.println("Corpus: not isCancelled, extractionCompleted: true");
			}

			return null;
		}

		public void resetProgress() {
			this.progress = 0.0;
			this.setProgress(0);
		}

		/*
		 * Increase the progress indicator bar by (double) step percent.
		 * 
		 */
		private void progress(double step) {
			this.progress += step;
			this.setProgress((int) Math.min(progress, 99));
		}

		@Override
		public void done() {
			notifyObserverExtractionEndOfProcess();
		}

	} // End : class CorpusExtraction

	public void createLPCAAndSABREClustering(ExtractionAlignments extractedAlignmentsSABRE, ExtractionAlignments extractedAlignmentsLPCA){

		//		System.out.println("---\nAlignments obtained by LPCA: " + extractedAlignmentsLPCA.getCurrentNumberOfAlignments());					
		//		System.out.println("Alignment obtained by SABRE: " +extractedAlignmentsSABRE.getCurrentNumberOfAlignments());	

		//		StandardView.getInstance().log("Alignments obtained by LPCA: " + extractedAlignmentsLPCA.getCurrentNumberOfAlignments());
		//		StandardView.getInstance().log("time LPCA: " + LPCATime + "ms");
		//		StandardView.getInstance().log("Alignments obtained by SABRE: " + extractedAlignmentsSABRE.getCurrentNumberOfAlignments());
		//		StandardView.getInstance().log("time SABRE: " + SABRETime + "ms");

		List<Coordinate<Integer>> alignmentsInSABREAndLPCA= new ArrayList<Coordinate<Integer>>();
		List<Coordinate<Integer>> alignmentsInLPCAandSABRE= new ArrayList<Coordinate<Integer>>();



		//		for(int i = 0 ; i < extractedAlignmentsSABRE.getAlignments().size() ; ++i){
		//			ArrayList<Alignment> al_a = extractedAlignmentsSABRE.getAlignments().get(i);
		//
		//			/* For each alignment of this score */
		//			for(int j = 0 ; j < al_a.size() ; ++j){
		//
		//				Alignment a = al_a.get(j);
		//
		//				if(a.getP1().getCAA().size() == 0){
		//					System.out.println("1Error: empty pattern");
		//				}
		//				if(a.getP2().getCAA().size() == 0){
		//					System.out.println("1Error: empty pattern");
		//				}
		//			}
		//		}
		//		
		//		for(int k = 0 ; k < extractedAlignmentsLPCA.getAlignments().size() ; ++k){
		//			ArrayList<Alignment> al_a2 = extractedAlignmentsLPCA.getAlignments().get(k);
		//
		//			/* For each alignment of that score */
		//			for(int l = 0 ; l < al_a2.size() ; ++l){
		//
		//				Alignment a2 = al_a2.get(l);
		//
		//				if(a2.getP1().getCAA().size() == 0){
		//					System.out.println("Error: empty pattern");
		//				}
		//				if(a2.getP2().getCAA().size() == 0){
		//					System.out.println("Error: empty pattern");
		//				}
		//			}
		//		}
		//		
		/** Find the alignments which have both been found by SABRE and LPCA */
		/* For each list of alignments extracted by SABRE which have a given score */
		for(int i = 0 ; i < extractedAlignmentsSABRE.getAlignments().size() ; ++i){
			ArrayList<Alignment> al_a = extractedAlignmentsSABRE.getAlignments().get(i);

			/* For each alignment of this score */
			for(int j = 0 ; j < al_a.size() ; ++j){

				Alignment a = al_a.get(j);

				/* For each list of alignments extracted by LPCA which have a given score */
				for(int k = 0 ; k < extractedAlignmentsLPCA.getAlignments().size() ; ++k){
					ArrayList<Alignment> al_a2 = extractedAlignmentsLPCA.getAlignments().get(k);

					/* For each alignment of that score */
					for(int l = 0 ; l < al_a2.size() ; ++l){

						Alignment a2 = al_a2.get(l);

						if(a.equals(a2)){
							alignmentsInSABREAndLPCA.add(new Coordinate<Integer>(i, j));
							alignmentsInLPCAandSABRE.add(new Coordinate<Integer>(k,l));
							//							System.out.println("---------\n");
							//							a.getP1().getPatternAA().display();
							//							System.out.println("\n+");
							//							a.getP2().getPatternAA().display();
							//							System.out.println("\n---\n");
							//							a2.getP1().getPatternAA().display();
							//							System.out.println("\n+");
							//							a2.getP2().getPatternAA().display();
						}
					}
				}
			}
		}

		List<Cluster> clusters1Locutor = new ArrayList<Cluster>(); 
		List<Cluster> clusters2Locutors = new ArrayList<Cluster>(); 

		/* Find the min and max score of alignments for each method */
		double minScore = Double.MAX_VALUE;
		double maxScore = -Double.MAX_VALUE;
		double moy = 0.0;
		double nb = 0.0;

		for(ArrayList<Alignment> al_a : extractedAlignmentsLPCA.getAlignments())
			for(Alignment a : al_a){
				nb++;
				moy += a.getScore();
				if(a.getScore() > maxScore)
					maxScore = a.getScore();
				if(a.getScore() < minScore)
					minScore = a.getScore();
			}

		//		StandardView.getInstance().log("Minimum score for LPCA: " + minScore);
		//		StandardView.getInstance().log("Maximum score for LPCA: " + maxScore);
		//		StandardView.getInstance().log("Average score for LPCA: " + (moy/nb));

		minScore = Double.MAX_VALUE;
		maxScore = -Double.MAX_VALUE;
		moy = 0.0;
		nb = 0.0;

		for(ArrayList<Alignment> al_a : extractedAlignmentsSABRE.getAlignments())
			for(Alignment a : al_a){
				nb++;
				moy += a.getScore();
				if(a.getScore() > maxScore)
					maxScore = a.getScore();
				if(a.getScore() < minScore)
					minScore = a.getScore();
			}

		//		StandardView.getInstance().log("Minimum score for SABRE: " + minScore);
		//		StandardView.getInstance().log("Maximum score for SABRE: " + maxScore);
		//		StandardView.getInstance().log("Average score for SABRE: " + (moy/nb));

		//		System.out.println("Number of irrelevant: " + results.getIrrelevantAlignments().size());
		//		System.out.println("Number of star2: " + results.getRelevantNonOptimalAlignments().size());
		//		System.out.println("Number of star: " + results.getOptimalAlignments().size());

		/* For each alignment found by SABRE */
		int id = 1;

		for(int i = 0 ; i < extractedAlignmentsSABRE.getAlignments().size() ; ++i){
			ArrayList<Alignment> al_a = extractedAlignmentsSABRE.getAlignments().get(i);

			for(int j = 0 ; j < al_a.size() ; ++j){

				Alignment a = al_a.get(j);
				a.setId(id);
				id++;

				Pattern p1 = a.getP1();
				Pattern p2 = a.getP2();

				Cluster c = new Cluster();
				c.add(p1);
				c.add(p2);

				/* Check if this cluster has already been saved */
				a.status = getStatus(a);

				/* Find where to add c */

				/* Find the number of locutors */
				int l1 = numberOfLocuteurs2(p1);
				int l2 = numberOfLocuteurs2(p2);
				boolean oneLocutor = false;

				if(l1 == 1 && l2 == 1)
					oneLocutor = true;

				/* Find if the alignment is contained also in LPCA */
				List<Cluster> addTo;

				/* If the alignment is contained in both SABRE and LPCA */
				if(alignmentsInSABREAndLPCA.contains(new Coordinate<Integer>(i,j))){

					a.setExtractionMethod(ExtractionMethod.BOTH);
				}

				if(oneLocutor)
					addTo = clusters1Locutor;
				else
					addTo = clusters2Locutors;

				c.setAlignment(a);
				addTo.add(c);

			}
		}

		/* For each alignment found by LPCA */
		int index = patterns.size();
		//		System.out.println("Patterns size: " + patterns.size());
		for(int i = 0 ; i < extractedAlignmentsLPCA.getAlignments().size() ; ++i){
			ArrayList<Alignment> al_a = extractedAlignmentsLPCA.getAlignments().get(i);

			for(int j = 0 ; j < al_a.size() ; ++j){

				Alignment a = al_a.get(j);
				a.setId(id);
				id++;

				Pattern p1 = a.getP1();
				Pattern p2 = a.getP2();

				p1.setIndex(index);
				index++;
				p2.setIndex(index);
				index++;

				/* For debugging */
				patterns.add(p1);
				patterns.add(p2);

				Cluster c = new Cluster();
				c.add(p1);
				c.add(p2);

				/* Check if this cluster has already been saved */
				a.status = getStatus(a);

				/* Find where to add c */

				/* Find the number of locutors */
				int l1 = numberOfLocuteurs2(p1);
				int l2 = numberOfLocuteurs2(p2);
				boolean oneLocutor = false;

				if(l1 == 1 && l2 == 1)
					oneLocutor = true;

				/* Find if the alignment is contained also in LPCA */
				List<Cluster> addTo;


				/* If the alignment is not contained in both SABRE and LPCA */
				if(!alignmentsInLPCAandSABRE.contains(new Coordinate<Integer>(i,j))){
					if(oneLocutor)
						addTo = clusters1Locutor;
					else
						addTo = clusters2Locutors;

					c.setAlignment(a);
					addTo.add(c);
				}

			}
		}

		/* For each extracted alignment (one alignment = 2 patterns) */
		for(int i = 0 ; i < patterns.size()/2 ; ++i){

			int id1 = 2*i;
			int id2 = id1+1;

			patterns.get(id1).setIndex(id1);
			patterns.get(id2).setIndex(id2);

			int[] bounds = patterns.get(id1).getBounds();

		}

		//		/* For each extracted alignment (one alignment = 2 patterns) */
		//		for(int i = 0 ; i < patternsLPCA.size()/2 ; ++i){
		//			
		//			int id1 = 2*i + patternsLPCA.size();
		//			int id2 = 2*i+1 + patternsLPCA.size();
		//			Pattern p1 = patternsLPCA.get(id1);
		//			Pattern p2 = patternsLPCA.get(id2);
		//
		//			patternsLPCA.get(id1).setIndex(id1);
		//			patternsLPCA.get(id2).setIndex(id2);
		//			
		//		}



		//		if(clusters1Locutor.size() > 0)
		{
			HardClusteringSolution hcsSABRE1 = new HardClusteringSolution(new ClusterSet(clusters1Locutor));
			hcsSABRE1.setMethodName("1 locutor (" + clusters1Locutor.size() + " alignments)");
			al_clusteringSolution.add(hcsSABRE1);
			//			System.out.println("SABRE1: " + clustersSABRE1.size());
			//			StandardView.getInstance().log("1 speaker (" + clusters1Locutor.size() + " alignments)");

			//			StandardView.getInstance().log("already saved: " + numberOfClustersAlreadySaved(clustersSABRE1));
		}

		//		if(clusters2Locutors.size() > 0)
		{
			HardClusteringSolution hcsSABRE2 = new HardClusteringSolution(new ClusterSet(clusters2Locutors));
			hcsSABRE2.setMethodName("2 locutors (" + clusters2Locutors.size() + " alignments)");
			al_clusteringSolution.add(hcsSABRE2);
			//			StandardView.getInstance().log("2 speakers (" + clusters2Locutors.size() + " alignments)");
			//			System.out.println("SABRE2: " + clustersSABRE2.size());

			//			StandardView.getInstance().log("already saved: " + numberOfClustersAlreadySaved(clustersSABRE2));
		}

		for(ClusteringSolution cs : al_clusteringSolution)
			notifyObserverClusters(cs);

		notifyObserverEndOfClusteringProcess();

		if(Corpus.getCorpus().results != null){
			Corpus.getCorpus().results.addAction(new ExtractionAction(LPCATime, SABRETime, clusters1Locutor, clusters2Locutors));
			Corpus.getCorpus().results.serialiseXMLFile();
		}


	}

	private Pattern.PatternStatus getStatus(Alignment a) {

		PatternStatus statuts = PatternStatus.UNKNOWN;
		
		ExperienceResults results = Corpus.getCorpus().results;
		
		if(MainTutorial.IS_TUTO)
			results = MainTutorial.results;

		if(results != null)
			if(results.getIrrelevantAlignments().contains(a)){
				statuts = PatternStatus.IRRELEVANT;
			}
			else if(results.getOptimalAlignments().contains(a)){
				statuts = PatternStatus.RELEVANT_AND_OPTIMAL;
			}
			else if(results.getRelevantNonOptimalAlignments().contains(a)){
				statuts = PatternStatus.RELEVANT_AND_NOT_OPTIMAL;
			}

		return statuts;		

	}

	/**
	 * Count the number of locutors from:
	 * - the first line which contain an annotation from the pattern; to
	 * - the last line which contains an annotation from the pattern.
	 * (i.e., if a line between these two lines does not contain any annotation in the pattern it will still be considered to compute the number of locutors)
	 * @param p
	 * @return
	 */
	public int numberOfLocuteurs(Pattern p){

		int result = 1;


		ArrayList<PointXMLSerializable> coord= p.getCoordinates();

		if(coord.size() > 0 ){
			String expert = "Expert";
			String autre = "Enquêté";
			String firstLocutor = "" ;

			boolean locuteurColumnFound = false;
			int col = 0;

			while(!locuteurColumnFound && col < aacf.getTotalNumberOfColumns()){

				if(aacf.getColumn(col) instanceof CommentColumn){

					firstLocutor = p.getOriginalAA().getAnnotation(coord.get(0).x, col);
					if(expert.equals(firstLocutor) || autre.equals(firstLocutor))
						locuteurColumnFound = true;
					else
						col++;

				}
				else
					col++;
			}

			if(locuteurColumnFound){


				int minLine = Integer.MAX_VALUE;
				int maxLine = -Integer.MAX_VALUE;

				/* For each annotation in the pattern */
				for(PointXMLSerializable c : coord){

					if(c.x > maxLine)
						maxLine = c.x;
					if(c.x < minLine)
						minLine = c.x;

				}

				int c = minLine;
				while(result == 1 && c <= maxLine){

					String c_locuteur = p.getOriginalAA().getAnnotation(c, col);

					if(!firstLocutor.equals(c_locuteur))
						result = 2;
					c++;
				}
			}
			else if(!MainTutorial.IS_TUTO)
				System.err.println("Corpus: Error locutor not found");

		}
		else
			System.out.println("Corpus: pattern size 0 id " + p.getIndex());

		return result;
	}

	/**
	 * Count the number of locutors in the lines which contain annotations from the pattern.
	 * (i.e., if a line does not contain any annotation in the pattern it will be ignored to compute the number of locutors)
	 * @param p
	 * @return
	 */
	public int numberOfLocuteurs2(Pattern p){

		int result = 1;


		ArrayList<PointXMLSerializable> coord= p.getCoordinates();

		if(coord.size() > 0 ){
			String expert = "Expert";
			String autre = "Enquêté";
			String firstLocutor = "" ;

			boolean locuteurColumnFound = false;
			int col = 0;

			while(!locuteurColumnFound && col < aacf.getTotalNumberOfColumns()){

				if(aacf.getColumn(col) instanceof CommentColumn){

					firstLocutor = p.getOriginalAA().getAnnotation(coord.get(0).x, col);
					if(expert.equals(firstLocutor) || autre.equals(firstLocutor))
						locuteurColumnFound = true;
					else
						col++;

				}
				else
					col++;
			}

			if(locuteurColumnFound){

				int minLine = Integer.MAX_VALUE;
				int maxLine = -Integer.MAX_VALUE;
				Set<Integer> coveredLines = new TreeSet<>();

				/* For each of the pattern coordinates */
				for(PointXMLSerializable c : coord)
					coveredLines.add(c.x);

				Iterator<Integer> iter = coveredLines.iterator();

				while(result == 1 && iter.hasNext()){

					Integer i = iter.next();

					String c_locuteur = p.getOriginalAA().getAnnotation(i, col);

					if(!firstLocutor.equals(c_locuteur))
						result = 2;
				}
			}
			else if(!MainTutorial.IS_TUTO)
				System.err.println("Corpus: Error locutor not found");

		}
		else
			System.out.println("Corpus: pattern size 0 id " + p.getIndex());

		return result;
	}

	/**
	 * Given <includedIn>, return the pattern which includes all the patterns
	 * which includes the pattern <id>
	 * 
	 * @param includedIn
	 *            Array whose size is the number of patterns and whose value for
	 *            a given pattern id correspond to the id of a pattern which
	 *            includes it. If no pattern includes the pattern the value is
	 *            -1.
	 * @param id
	 *            Id of the pattern from which you seek the parent
	 * @return
	 */
	private int getParentPattern(int[] includedIn, int id) {
		int previousParentId = id;
		int currentParentId = includedIn[previousParentId];

		while (currentParentId != -1) {
			previousParentId = currentParentId;
			currentParentId = includedIn[previousParentId];
		}

		return previousParentId;

	}

	public int getIndexOfPattern(Pattern p) {
		return patterns.indexOf(p);
	}

	/**
	 * Test if the program is ready to process
	 * 
	 * @return True if the corpus contain AnnotatedArrays, parameters for the
	 *         extraction algorithme SABRE and if the column format contains
	 *         annotation column with the proper parameter (if numerical columns
	 *         are considered, the maxDist must be specified; if annotation
	 *         columns are considered, a positive score table must be specified)
	 */
	public boolean isReadyToProcess() {

		return (aacf != null && ((aacf.containColumnsOfType(ColumnType.ANNOTATION) && AnnotationColumn.pst != null)
				))
				&& arrays != null && SABRE.getInstance().getParam() != null
				&& SABRE.getInstance().getParam().desired_number_of_alignments > 0;
	}

	/**
	 * Change the column format of the corpus
	 * 
	 * @param new_aacf
	 *            New column format
	 * @param update
	 *            True if the annotated arrays in the corpus must be updated
	 *            according to the new format (otherwise the annotated arrays
	 *            are removed)
	 */
	public void setAACF(AAColumnFormat new_aacf, boolean update) {

		List<String> old_paths = new ArrayList<String>();

		/* Remove all the arrays and add their path in <old_paths> */
		if (arrays != null) {

			for (int i = arrays.size() - 1; i >= 0; i--) {
				old_paths.add(arrays.get(i).getFullPath());
				arrays.remove(i);
				notifyObserverRemoveAA(i);
			}

			arrays = null;
		}

		/* Update the column format */
		aacf = new_aacf;

		/* If the annotated arrays previously in the corpus must be updated */
		if (update) {

			arrays = new ArrayList<AnnotatedArray>();

			try {
				/* For each of the previous path */
				for (String s : old_paths)
					add(s, Main.hasHeader);

			} catch (UndefinedColumnFormatException e) {
				e.printStackTrace();
			}

		}

	}

	public boolean isColumnFormatDefined() {
		return aacf != null;
	}

	public boolean isColumnHeaderDefined(){
		return aacf.getColumnHeader() != null;
	}

	public String getColumnHeader(int i){
		return aacf.getColumnHeader().get(i);
	}

	public void setColumnHeader(ArrayList<String> ch){
		aacf.setColumnHeader(ch);
	}

	public int getTotalNumberOfColumns() {
		return aacf.getTotalNumberOfColumns();
	}

	public ColumnType getColumnType(int i) {
		return aacf.getColumn(i).getType();
	}

	public AbstractColumn<?> getColumn(int i) {
		return aacf.getColumn(i);
	}

	public PositionedColumn getPositionedColumn(int i) {
		return aacf.getPositionedColumn(i);
	}

	public int getNumberOfAnnotations(){
		return annotations.size();
	}

	public void encodeToFile(Object object, String fileName) throws FileNotFoundException, IOException {

		XMLEncoder encoder = new XMLEncoder(new FileOutputStream(fileName));
		try {
			encoder.writeObject(object);
			encoder.flush();
		} finally {
			encoder.close();
		}
	}

	public Object decodeFromFile(String fileName) throws FileNotFoundException, IOException {
		Object object = null;

		XMLDecoder decoder = new XMLDecoder(new FileInputStream(fileName));
		try {
			object = decoder.readObject();
		} finally {
			decoder.close();
		}
		return object;
	}

}
