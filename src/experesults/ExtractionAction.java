package experesults;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import clustering.Cluster;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class  ExtractionAction extends Action {

	@XmlAttribute
	private long LPCADuration;
	
	@XmlAttribute
	private long SABREDuration;
	
	public long getLPCADuration() {
		return LPCADuration;
	}

	public void setLPCADuration(long lPCADuration) {
		LPCADuration = lPCADuration;
	}

	public long getSABREDuration() {
		return SABREDuration;
	}

	public void setSABREDuration(long sABREDuration) {
		SABREDuration = sABREDuration;
	}

	public List<Cluster> getOneLocutor() {
		return oneLocutor;
	}

	public void setOneLocutor(List<Cluster> oneLocutor) {
		this.oneLocutor = oneLocutor;
	}


	public List<Cluster> getTwoLocutors() {
		return twoLocutors;
	}

	public void setTwoLocutors(List<Cluster> twoLocutors) {
		this.twoLocutors = twoLocutors;
	}

	public int getNbOfOptimalPatterns1Locutor() {
		return nbOfOptimalPatterns1Locutor;
	}

	public void setNbOfOptimalPatterns1Locutor(int nbOfOptimalPatterns1Locutor) {
		this.nbOfOptimalPatterns1Locutor = nbOfOptimalPatterns1Locutor;
	}

	public int getNbOfRelevantNonOptimalPatterns1Locutor() {
		return nbOfRelevantNonOptimalPatterns1Locutor;
	}


	public void setNbOfRelevantNonOptimalPatterns1Locutor(int nbOfRelevantNonOptimalPatterns1Locutor) {
		this.nbOfRelevantNonOptimalPatterns1Locutor = nbOfRelevantNonOptimalPatterns1Locutor;
	}


	public int getNbOfIrrelevantPatterns1Locutor() {
		return nbOfIrrelevantPatterns1Locutor;
	}


	public void setNbOfIrrelevantPatterns1Locutor(int nbOfIrrelevantPatterns1Locutor) {
		this.nbOfIrrelevantPatterns1Locutor = nbOfIrrelevantPatterns1Locutor;
	}


	public int getNbOfOptimalPatterns2Locutors() {
		return nbOfOptimalPatterns2Locutors;
	}


	public void setNbOfOptimalPatterns2Locutors(int nbOfOptimalPatterns2Locutors) {
		this.nbOfOptimalPatterns2Locutors = nbOfOptimalPatterns2Locutors;
	}


	public int getNbOfRelevantNonOptimalPatterns2Locutors() {
		return nbOfRelevantNonOptimalPatterns2Locutors;
	}


	public void setNbOfRelevantNonOptimalPatterns2Locutors(int nbOfRelevantNonOptimalPatterns2Locutors) {
		this.nbOfRelevantNonOptimalPatterns2Locutors = nbOfRelevantNonOptimalPatterns2Locutors;
	}


	public int getNbOfIrrelevantPatterns2Locutors() {
		return nbOfIrrelevantPatterns2Locutors;
	}


	public void setNbOfIrrelevantPatterns2Locutors(int nbOfIrrelevantPatterns2Locutors) {
		this.nbOfIrrelevantPatterns2Locutors = nbOfIrrelevantPatterns2Locutors;
	}


	@XmlElement
	private List<Cluster> oneLocutor;

	@XmlElement
	private List<Cluster> twoLocutors;

	@XmlAttribute
	private int nbOfOptimalPatterns1Locutor;

	@XmlAttribute
	private int nbOfRelevantNonOptimalPatterns1Locutor = 0;

	@XmlAttribute
	private int nbOfIrrelevantPatterns1Locutor = 0;

	@XmlAttribute
	private int nbOfOptimalPatterns2Locutors = 0;

	@XmlAttribute
	private int nbOfRelevantNonOptimalPatterns2Locutors = 0;

	@XmlAttribute
	private int nbOfIrrelevantPatterns2Locutors = 0;
	
	public ExtractionAction(){}
	
	public ExtractionAction(long LPCADuration, long SABREDuration, List<Cluster> oneLocutor, List<Cluster> twoLocutors) {
		
		this.oneLocutor = oneLocutor;
		this.twoLocutors = twoLocutors;
		this.LPCADuration = LPCADuration;
		this.SABREDuration = SABREDuration;

		for(Cluster c: oneLocutor){
			
			switch(c.getAlignment().status){
			case IRRELEVANT: nbOfIrrelevantPatterns1Locutor++; break;
			case RELEVANT_AND_NOT_OPTIMAL: nbOfRelevantNonOptimalPatterns1Locutor++; break;
			case RELEVANT_AND_OPTIMAL: nbOfOptimalPatterns1Locutor++; break;
			}
		}
		
		for(Cluster c: twoLocutors){
			
			switch(c.getAlignment().status){
			case IRRELEVANT: nbOfIrrelevantPatterns2Locutors++; break;
			case RELEVANT_AND_NOT_OPTIMAL: nbOfRelevantNonOptimalPatterns2Locutors++; break;
			case RELEVANT_AND_OPTIMAL: nbOfOptimalPatterns2Locutors++; break;
			}
		}
		
	}


	@Override
	public String getDescription() {
		return "Performed an extraction. SABRE: " + SABREDuration + ", LPCA: " + LPCADuration + ". " + oneLocutor.size() + " one locutor pattern extracted and " + twoLocutors.size() + " two locutors patterns extracted." ;
	}
	
	

	
	
	
	
}
