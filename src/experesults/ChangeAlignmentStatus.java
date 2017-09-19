package experesults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import model.Alignment;
import model.Pattern;
import model.Pattern.PatternStatus;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ChangeAlignmentStatus extends Action{

	@XmlAttribute
	private Pattern.PatternStatus status;
	
	@XmlElement
	private Alignment a;
	
	public ChangeAlignmentStatus(){}
	
	public ChangeAlignmentStatus(Alignment a, PatternStatus status) {
		this.a = a;
		this.status = status;
	}

	public Pattern.PatternStatus getStatus() {
		return status;
	}

	public void setStatus(Pattern.PatternStatus status) {
		this.status = status;
	}

	public Alignment getA() {
		return a;
	}

	public void setA(Alignment a) {
		this.a = a;
	}

	@Override
	public String getDescription() {
		return "Change an alignment status to " + status;
	}


}
