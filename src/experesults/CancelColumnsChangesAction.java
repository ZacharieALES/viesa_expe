package experesults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelColumnsChangesAction extends Action{

	@Override
	public String getDescription() {
		return "Cancel columns changes";
	}

}
