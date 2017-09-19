package experesults;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidateColumnsChangesAction extends Action{

	@Override
	public String getDescription() {
		return "Validate columns changes";
	}

}
