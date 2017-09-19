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

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import extraction.SABRE;

/**
 * Contains an ArrayList of 2D coordinates which represent a pattern in an
 * AnnotatedArray
 * 
 * @author Zach
 *
 */
public class Pattern implements Serializable {
	
	public enum PatternStatus{
		UNKNOWN, IRRELEVANT, RELEVANT_AND_OPTIMAL, RELEVANT_AND_NOT_OPTIMAL
	}

	private static final long serialVersionUID = -3193545608371129865L;

	/* aa from which the Pattern have been extracted */
	private AnnotatedArray originalAA;

	/*
	 * aa which correspond to the pattern, annotations of originalaa which are
	 * not in the Pattern are replaced by empty annotations
	 */
	@XmlTransient
	private AnnotatedArray patternAA;

	@XmlElement
	public String fileName;
	
	@XmlElement
	public String fullPath;
	
	@XmlTransient
	public Alignment alignment = null;
	

	/**
	 * Coordinates of the annotations in the AnnotatedArray which compose the
	 * Pattern 
	 **/
	private ArrayList<PointXMLSerializable> cAA;


	/** Allows quick identification of a pattern **/
	@XmlTransient
	private int index = -1;

	/** Enable to save the coordinate in a smaller xml file */ 
	private int[] coordinateForXMLSave;	

	public int[] getCoordinateForXMLSave() {
		return coordinateForXMLSave;
	}

	@XmlAttribute
	public void setCoordinateForXMLSave(int[] coordinateForXMLSave) {
		this.coordinateForXMLSave = coordinateForXMLSave;
	}
	
	public void prepareToXMLSave(){
		
		coordinateForXMLSave = new int [cAA.size() * 2];
		
		int id = 0;
		
		for(PointXMLSerializable p: cAA){

			coordinateForXMLSave[id] = p.x;
			coordinateForXMLSave[id+1] = p.y;
			
			id += 2;
		}
		
	}
	
	public void restoreFromXMLSave(){
		
		cAA = new ArrayList<>();
		
		for(int id = 0 ; id < coordinateForXMLSave.length ; id += 2)
			cAA.add(new PointXMLSerializable(coordinateForXMLSave[id], coordinateForXMLSave[id+1]));
	}
	
	@XmlTransient
	public void setcAA(ArrayList<PointXMLSerializable> caa){this.cAA = caa;}

	@XmlTransient
	public void setPatternAA(AnnotatedArray patternAA) {
		this.patternAA = patternAA;
	}

	@XmlElement
	public void setOriginalAA(AnnotatedArray originalAA) {
		this.originalAA = originalAA;
	}

	public Pattern(AnnotatedArray aa, ArrayList<PointXMLSerializable> c) {

		originalAA = aa;
		fullPath = aa.getFullPath();
		fileName = aa.getFileName();
		cAA = new ArrayList<PointXMLSerializable>(c);

	}

	public Pattern() {
		cAA = new ArrayList<PointXMLSerializable>();
		originalAA = null;
		index = 0;
	}

	/* Constructor for second part of an alignment */

	public ArrayList<PointXMLSerializable> getCoordinates() {
		return cAA;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int i) {
		index = i;
	}

	public AnnotatedArray getOriginalAA() {
		return originalAA;
	}

	/**
	 * Get an AnnotatedArray which correspond to the current Pattern.
	 * 
	 * @return
	 */
	public AnnotatedArray getPatternAA() {

		if (originalAA != null) {

			/* If the pattern AA have not already been created */
			if (patternAA == null){
				
				/* Split the coordinates according to their column (coordinatesByColumns.get(i) will contains the id of each row of column i which is in the pattern) */
				ArrayList<ArrayList<Integer>> coordinatesByColumns = new ArrayList<ArrayList<Integer>>();
				
				/* Create a list for each column */
				for(int i = 0 ; i < originalAA.getNumberOfAnnotationColumns() ; ++i)
					coordinatesByColumns.add(new ArrayList<Integer>());
				
				/* Add the row number of each coordinate in the list which corresponds to its column */
				for(PointXMLSerializable c : cAA)
					coordinatesByColumns.get(c.y).add(c.x);
				
				
				int[] bounds = getBounds();
				
				System.out.println("Pattern bounds: " + bounds[0] + " / " + bounds[1]);
				
				/* Create the columns with the proper size and filled with empty annotations */
				ArrayList<AbstractColumn<?>> result = new ArrayList<AbstractColumn<?>>();

				/* For each column */
				for (int j = 0; j < originalAA.getNumberOfAnnotationColumns(); j++){
					
					/* Create the columns */
					result.add(originalAA.getAnnotationColumn(j).createSubColumn(bounds, coordinatesByColumns.get(j)));
					
				}

 				patternAA = new AnnotatedArray(result);
			}
		}

		return patternAA;
	}

	/**
	 * Get the location of the pattern in its original AnnotatedArray
	 * 
	 * @return Array of two int which correspond to : - result[0] : first line
	 *         of <originalAA> which contains an Annotation of the current
	 *         Pattern - result[1] : last line of <originalAA> which contains an
	 *         Annotation of the current Pattern
	 */
	public int[] getBounds() {

		int[] result = new int[2];
		if(originalAA.annotations.size() > 0)
			result[0] = originalAA.annotations.get(0).values.size();
		result[1] = -1;

		if (cAA != null) {

			for (int i = 0; i < cAA.size(); i++) {
				
				if (cAA.get(i).x > result[1])
					result[1] = cAA.get(i).x;

				if (cAA.get(i).x < result[0])
					result[0] = cAA.get(i).x;
			}

		} else
			System.err.println("Can't get the lower bound: <cAA> is null");

		return result;
	}
	
	public void setAA(AnnotatedArray aa){
		this.originalAA = aa;
	}

	public double similarity(Pattern p) {

		return SABRE.getInstance().similarity(this, p);

	}

	@Override
	public String toString() {

		StringBuffer output = new StringBuffer();

		AnnotatedArray oAA = getPatternAA();

		int numberOfLines = 0;
		boolean containAnnotationColumns = oAA.annotations != null && oAA.annotations.size() > 0;
	
		if(containAnnotationColumns && oAA.annotations.get(0) != null && oAA.annotations.get(0).values != null)
			numberOfLines = oAA.annotations.get(0).values.size();
	
		NumberFormat nf = NumberFormat.getInstance() ;
		nf.setMaximumFractionDigits(2); // au plus 2 chiffres apres la virgule
		nf.setMinimumFractionDigits(2); // exactement  chiffres après la virgule

		/* For each line of the aligned pattern */
		for(int i = 0 ; i < numberOfLines ; ++i){

			if(containAnnotationColumns){
				/* For each annotation column of the aligned pattern */
				for (int j = 0; j < oAA.annotations.size(); j++) {
					int k = oAA.annotations.get(j).values.get(i);

					if(k != 0)
						output.append(Corpus.getCorpus().getAnnotation(k) + "\t");
					else
						output.append("*\t");
				}
			}

			output.append("\n");
		}

		return output.toString();
	}

	public ArrayList<PointXMLSerializable> getCAA(){
		return cAA;
	}
	
	public void setCAA(ArrayList<PointXMLSerializable> ca){
		cAA = ca;
	}

	public boolean isIncludedIn(Pattern p2) {

		boolean result = false;

		/* If the two patterns correspond to the same annotated element */
		if (this.fullPath.equals(p2.fullPath)) {

			/* For each coordinate in this pattern */
			int id1 = 0;
			boolean foundNonIncludedCoordinate = false;

			while (id1 < this.cAA.size() && !foundNonIncludedCoordinate) {

				/* For each coordinate in p2 */
				PointXMLSerializable c1 = cAA.get(id1);
				int id2 = 0;
				boolean isIncluded = false;

				while (id2 < p2.cAA.size() && !foundNonIncludedCoordinate
						&& !isIncluded) {

					PointXMLSerializable c2 = p2.cAA.get(id2);

					if (c1.equals(c2))
						isIncluded = true;

					id2++;
				}

				if (!isIncluded)
					foundNonIncludedCoordinate = true;

				id1++;
			}

			if (!foundNonIncludedCoordinate)
				result = true;

		}

		return result;
	}
	


	/**
	 * Coordinates added by the expert to make the alignment optimal
	 */
	@XmlElementWrapper
	@XmlElement (name = "PointXMLSerializable")
	public List<PointXMLSerializable> expertAddedCoordinates = null;
	


	/**
	 * Coordinates removed by the expert to make the alignment optimal
	 */
	@XmlElementWrapper
	@XmlElement (name = "PointXMLSerializable")
	public List<PointXMLSerializable> expertRemovedCoordinates = null;
	

	
	public boolean isEditedByExpert(){
		return expertAddedCoordinates != null && expertAddedCoordinates.size() > 0
				|| expertRemovedCoordinates!= null && expertRemovedCoordinates.size() > 0;
	}
	
}
