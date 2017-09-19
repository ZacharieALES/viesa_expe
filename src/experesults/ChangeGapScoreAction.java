package experesults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ChangeGapScoreAction extends Action{

	
	@XmlAttribute
	private double newScore;
	
	public ChangeGapScoreAction(){}
	
	public ChangeGapScoreAction(double newScore) {
		this.newScore = newScore;
	}

	public double getNewScore() {
		return newScore;
	}

	public void setNewScore(double newScore) {
		this.newScore = newScore;
	}

	@Override
	public String getDescription() {
		return "Change gap score to " + newScore;
	}

}
