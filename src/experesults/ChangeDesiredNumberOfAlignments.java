package experesults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ChangeDesiredNumberOfAlignments extends Action{

	@XmlAttribute
	private int newNumber;
	
	public ChangeDesiredNumberOfAlignments(){}
	
	public ChangeDesiredNumberOfAlignments(int newNumber) {
		this.newNumber = newNumber;
	}

	public int getNewNumber() {
		return newNumber;
	}

	public void setNewNumber(int newNumber) {
		this.newNumber = newNumber;
	}

	@Override
	public String getDescription() {
		return "Change desired number of alignments to " + newNumber;
	}

}
