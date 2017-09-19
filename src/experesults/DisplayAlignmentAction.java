package experesults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import model.Alignment;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DisplayAlignmentAction extends Action{

	@XmlElement
	private Alignment a;
	
	public DisplayAlignmentAction(){}
	
	public DisplayAlignmentAction(Alignment a) {
		this.a = a;
	}

	public Alignment getA() {
		return a;
	}

	public void setA(Alignment a) {
		this.a = a;
	}

	@Override
	public String getDescription() {
		return "Display alignment number " + a.getId();
	}

}
