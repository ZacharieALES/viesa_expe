package model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import model.Pattern.PatternStatus;

/**
 * An alignment corresponds to two patterns extracted together
 * @author zach
 *
 */
/**
 * @author zach
 *
 */
public class Alignment {

	private Pattern p1;
	private Pattern p2;

	@XmlAttribute
	public PatternStatus status = PatternStatus.UNKNOWN;

	private double score;

	private int id;

	public Alignment(){}

	public enum ExtractionMethod{
		SABRE,
		LPCA,
		BOTH;

	}

	private ExtractionMethod extractionMethod;

	public Alignment(Pattern p1, Pattern p2, double score, ExtractionMethod em){
		this.p1 = p1;
		this.p2 = p2;

		p1.alignment = this;
		p2.alignment = this;

		this.score = score;
		this.extractionMethod = em;
		
	}

	public int getId() {
		return id;
	}

	@XmlAttribute
	public void setId(int id) {
		this.id = id;
	}

	public ExtractionMethod getExtractionMethod(){
		return extractionMethod;
	}

	@XmlElement
	public void setExtractionMethod(ExtractionMethod em){
		extractionMethod = em;
	}

	public Pattern getP1() {
		return p1;
	}

	@XmlElement
	public void setP1(Pattern p1) {
		this.p1 = p1;
	}

	public Pattern getP2() {
		return p2;
	}

	@XmlElement
	public void setP2(Pattern p2) {
		this.p2 = p2;
	}

	public double getScore() {
		return score;
	}

	@XmlAttribute
	public void setScore(double s){
		score = s;
	}

	@Override
	public boolean equals(Object o){

		boolean result = false;

		if(o instanceof Alignment){
			
			Alignment a = (Alignment)o;
			boolean b11 = getP1().isIncludedIn(a.getP1()) && a.getP1().isIncludedIn(getP1());
			boolean b12 = getP1().isIncludedIn(a.getP2()) && a.getP2().isIncludedIn(getP1());
			boolean b21 = getP2().isIncludedIn(a.getP1()) && a.getP1().isIncludedIn(getP2());
			boolean b22 = getP2().isIncludedIn(a.getP2()) && a.getP2().isIncludedIn(getP2());
			
			result = (b11 && b22) || (b12 && b21);
		}
		


		return result;
	}

	public boolean isEditedByExpert() {
		return p1.isEditedByExpert() || p2.isEditedByExpert();
	}

	@Override
	public String toString(){ return "alignment n°"+getId();}
}
