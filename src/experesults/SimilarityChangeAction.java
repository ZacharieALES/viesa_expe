package experesults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement 
@XmlAccessorType(XmlAccessType.FIELD)
public class SimilarityChangeAction extends Action{

	@XmlAttribute
	private String a1;

	@XmlAttribute
	private String a2;

	@XmlAttribute
	private Double newValue;
	
	public SimilarityChangeAction(){}
	
	public SimilarityChangeAction(String a1, String a2, Double newValue) {
		
		this.a1 = a1;
		this.a2 = a2;
		this.newValue = newValue;
		
	}

	public String getA1() {
		return a1;
	}

	public void setA1(String a1) {
		this.a1 = a1;
	}

	public String getA2() {
		return a2;
	}

	public void setA2(String a2) {
		this.a2 = a2;
	}

	public Double getNewValue() {
		return newValue;
	}

	public void setNewValue(Double newValue) {
		this.newValue = newValue;
	}

	@Override
	public String getDescription() {
		return "sim(" + a1 + ", " + a2 + ") set to " + newValue;
	}

}
