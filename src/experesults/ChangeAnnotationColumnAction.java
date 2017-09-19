package experesults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import model.AAColumnFormat.ColumnType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ChangeAnnotationColumnAction extends Action{

	@XmlAttribute
	private int positionInCSV;

	@XmlAttribute
	private ColumnType newType;
	
	public ChangeAnnotationColumnAction(){}
	
	public ChangeAnnotationColumnAction(int positionInCSV, ColumnType newType) {
		this.positionInCSV = positionInCSV;
		this.newType = newType;
	}

	public int getPositionInCSV() {
		return positionInCSV;
	}

	public void setPositionInCSV(int positionInCSV) {
		this.positionInCSV = positionInCSV;
	}

	public ColumnType getNewType() {
		return newType;
	}

	public void setNewType(ColumnType newType) {
		this.newType = newType;
	}

	@Override
	public String getDescription() {
		return "Change annotation column number " + positionInCSV + " to " + newType;
	}

}
