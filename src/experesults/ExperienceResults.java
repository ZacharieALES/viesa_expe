package experesults;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountedCompleter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import exception.CSVSeparatorNotFoundException;
import exception.InvalidArgumentsToCreateAnAAColumnFormat;
import exception.InvalidCSVFileNumberOfColumnsException;
import exception.InvalidInputFileException;
import exception.InvalidNumberOfColumnsInInputFilesException;
import main.MainTutorial;
import model.AAColumnFormat;
import model.Alignment;
import model.AnnotatedArray;
import model.AnnotationColumn;
import model.CommentColumn;
import model.Corpus;
import model.Pattern;
import model.PositionedColumn;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExperienceResults {

	public static int actionCounter = 0;

	@XmlAttribute
	String corpusName;

	@XmlAttribute
	String saveFolderPath;

	@XmlAttribute
	String csvPath;

	@XmlAttribute
	int initialDesiredNumberOfAlignments;

	@XmlAttribute
	double initialGapCost;

	@XmlElement
	List<AnnotatedArray> arrays;

	@XmlElementRef
	private List<Action> actions;


	@XmlElementWrapper
	@XmlElement (name = "Alignment")
	private List<Alignment> optimalAlignments = new ArrayList<>();


	@XmlElementWrapper
	@XmlElement (name = "Alignment")
	private List<Alignment> relevantNonOptimalAlignments = new ArrayList<>();


	@XmlElementWrapper
	@XmlElement (name = "Alignment")
	private List<Alignment> irrelevantAlignments = new ArrayList<>();

	public ExperienceResults(){}

	public ExperienceResults(String corpusName, String saveFolderPath, String csvPath, int initialDesiredNumberOfAlignments, double initialGapCost, List<AnnotatedArray> arrays, List<Integer> commentColumns, List<Integer> annotationColumns){
		this.corpusName = corpusName;
		this.initialDesiredNumberOfAlignments = initialDesiredNumberOfAlignments;
		this.initialGapCost = initialGapCost;
		this.saveFolderPath = saveFolderPath;
		this.csvPath = csvPath;
		this.arrays = arrays;

		File f = new File(saveFolderPath + "/" + corpusName + ".xml");
		File folder = new File(saveFolderPath);

		if(!folder.exists() && !MainTutorial.IS_TUTO)
			folder.mkdirs();

		if(f.exists()){

			//create file input stream
			InputStream is;
			try {

				is = new FileInputStream(f.getPath());

				//XML and Java binding 
				JAXBContext jaxbContext = JAXBContext.newInstance(ExperienceResults.class, CancelColumnsChangesAction.class, CancelSimChangesAction.class, ChangeAlignmentStatus.class, ChangeAnnotationColumnAction.class, ChangeDesiredNumberOfAlignments.class, ChangeGapScoreAction.class, DisplayAlignmentAction.class, ExperienceResults.class, ExtractionAction.class, SimilarityChangeAction.class, ValidateColumnsChangesAction.class, ValidateSimChangesAction.class, StartSoftwareAction.class, CloseSoftwareAction.class);

				//class responsible for the process of deserializing 
				//XML data into Java object
 				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				ExperienceResults previousRes = (ExperienceResults) jaxbUnmarshaller.unmarshal(is);

				actions = previousRes.actions;
				optimalAlignments = previousRes.optimalAlignments;
				relevantNonOptimalAlignments = previousRes.relevantNonOptimalAlignments;
				irrelevantAlignments = previousRes.irrelevantAlignments;

				List<AnnotatedArray> newAA = new ArrayList<>();

				ArrayList<PositionedColumn> lpc = new ArrayList<>();

				for(Integer i: commentColumns)
					lpc.add(new PositionedColumn(new CommentColumn(), i));

				for(Integer i: annotationColumns)
					lpc.add(new PositionedColumn(new AnnotationColumn(), i));

				AAColumnFormat aacf;
				try {
					aacf = new AAColumnFormat(lpc);

//					/* For each classified annotated array */
//					for(AnnotatedArray aa: previousRes.arrays){
//
//						/* Recreate the annotated array (as only the filename has been saved (i.e., not the annotations) in the XML file) */
//						newAA.add(new AnnotatedArray(aa.getFullPath(), true, aacf));
//
//					}
				} catch (InvalidArgumentsToCreateAnAAColumnFormat e1) {
					e1.printStackTrace();
				}
//					catch (CSVSeparatorNotFoundException e) {
//					e.printStackTrace();
//				} catch (InvalidNumberOfColumnsInInputFilesException e) {
//					e.printStackTrace();
//				} catch (InvalidCSVFileNumberOfColumnsException e) {
//					e.printStackTrace();
//				} catch (InvalidInputFileException e) {
//					e.printStackTrace();
//				}

				arrays = newAA;

				List<Alignment> la = new ArrayList<>();
				la.addAll(optimalAlignments);
				la.addAll(this.relevantNonOptimalAlignments);
				la.addAll(this.irrelevantAlignments);

				for(Alignment a: la){

					Pattern[] ap = new Pattern[2];
					ap[0] = a.getP1();
					ap[1] = a.getP2();
					/* For each pattern */
					for(Pattern p: ap){

						p.restoreFromXMLSave();

						AnnotatedArray result = null;
						/* Associate its corresponding original annotated array */
						int i = 0;

						while(result == null && i < Corpus.getCorpus().getAASize()){

							if(Corpus.getCorpus().getAA().get(i).getFullPath().contains(p.fileName))
								result = Corpus.getCorpus().getAA().get(i);

							++i;
						}

						if(result != null)
							p.setOriginalAA(result);
						else{
							System.err.println("Error: annotated array not found in class.\n\tPath according to the pattern: " + p.fileName + "\n\tPath of the annotated arrays: ");
							for(AnnotatedArray caa: Corpus.getCorpus().getAA())
								System.err.println(caa.getFullPath());
						}


					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			}

		}
		else{
			actions = new ArrayList<>();
			optimalAlignments = new ArrayList<>();
			relevantNonOptimalAlignments = new ArrayList<>();
			irrelevantAlignments = new ArrayList<>();
		}

	}

	public void addAction(Action action) {
		
		if(actions == null)
			actions = new ArrayList<>();
		
		actions.add(action);
//		System.out.println(action.toString());

		if(action instanceof ChangeAlignmentStatus){
			
			actionCounter++;

			if(actionCounter % 10 == 0)
				new Thread(){

				@Override
				public void start(){
					if(Corpus.getCorpus().results != null)
						Corpus.getCorpus().results.serialiseXMLFile();	
				}

			}.run();
		}
	}

	public String getCorpusName() {
		return corpusName;
	}

	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}

	public String getSaveFolderPath() {
		return saveFolderPath;
	}

	public void setSaveFolderPath(String saveFolderPath) {
		this.saveFolderPath = saveFolderPath;
	}

	public String getCsvPath() {
		return csvPath;
	}

	public void setCsvPath(String csvPath) {
		this.csvPath = csvPath;
	}

	public int getInitialDesiredNumberOfAlignments() {
		return initialDesiredNumberOfAlignments;
	}

	public void setInitialDesiredNumberOfAlignments(int initialDesiredNumberOfAlignments) {
		this.initialDesiredNumberOfAlignments = initialDesiredNumberOfAlignments;
	}

	public double getInitialGapCost() {
		return initialGapCost;
	}

	public void setInitialGapCost(double initialGapCost) {
		this.initialGapCost = initialGapCost;
	}

	public List<AnnotatedArray> getArrays() {
		return arrays;
	}

	public void setArrays(List<AnnotatedArray> arrays) {
		this.arrays = arrays;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<Action> getActions() {
		return actions;
	}

	public List<Alignment> getOptimalAlignments() {
		return optimalAlignments;
	}

	public void setOptimalAlignments(List<Alignment> optimalAlignments) {
		this.optimalAlignments = optimalAlignments;
	}

	public List<Alignment> getRelevantNonOptimalAlignments() {
		return relevantNonOptimalAlignments;
	}

	public void setRelevantNonOptimalAlignments(List<Alignment> relevantNonOptimalAlignments) {
		this.relevantNonOptimalAlignments = relevantNonOptimalAlignments;
	}

	public List<Alignment> getIrrelevantAlignments() {
		return irrelevantAlignments;
	}

	public void setIrrelevantAlignments(List<Alignment> irrelevantAlignments) {
		this.irrelevantAlignments = irrelevantAlignments;
	}

	public void serialiseXMLFile(){

		File t_folder = new File(saveFolderPath);

		/* If the folder does not exist, create it */
		if(!t_folder.exists())
			t_folder.mkdir();

		/* If the folder already exists */
		else{

			/* Remove all the previously saved temp files which correspond to the same characteristics 
			 * (to only keep the more recent one) */
			File[] subfiles = t_folder.listFiles();

			for(int i=0 ; i<subfiles.length; i++)
				if(subfiles[i].getName().contains(corpusName))
					subfiles[i].delete();
		}

		String saveFile = saveFolderPath + "/" + corpusName + ".xml";

		try {

			List<Alignment> la = new ArrayList<>();
			la.addAll(optimalAlignments);
			la.addAll(this.relevantNonOptimalAlignments);
			la.addAll(this.irrelevantAlignments);

			for(Alignment a: la){

				Pattern[] ap = new Pattern[2];
				ap[0] = a.getP1();
				ap[1] = a.getP2();
				/* For each pattern */
				for(Pattern p: ap){

					p.alignment = a;
					p.prepareToXMLSave();
				}
			}

			JAXBContext jaxbContext = JAXBContext.newInstance(ExperienceResults.class, CancelColumnsChangesAction.class, CancelSimChangesAction.class, ChangeAlignmentStatus.class, ChangeAnnotationColumnAction.class, ChangeDesiredNumberOfAlignments.class, ChangeGapScoreAction.class, DisplayAlignmentAction.class, ExperienceResults.class, ExtractionAction.class, SimilarityChangeAction.class, ValidateColumnsChangesAction.class, ValidateSimChangesAction.class, StartSoftwareAction.class, CloseSoftwareAction.class);

			//class responsible for the process of 
			//serializing Java object into XML data
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			//marshalled XML data is formatted with linefeeds and indentation
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			//specify the xsi:schemaLocation attribute value 
			//to place in the marshalled XML output
			//			jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, 
			//					"http://www.mysamplecode.com/ws/v10 OrderService_v10.xsd");

			//			//send to console
			//			jaxbMarshaller.marshal(this, System.out);
			//send to file system
			OutputStream os = new FileOutputStream(saveFile);
			jaxbMarshaller.marshal(this, os );

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
