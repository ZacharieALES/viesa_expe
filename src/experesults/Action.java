package experesults;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Action {
	
//	@XmlAttribute
//	private long time;
//	
//	public long getTime() {
//		return time;
//	}
//
//	public void setTime(long time) {
//		this.time = time;
//	}
	
	@XmlElement
	private String date = "";
	
	public String getDate(){
		return date;
	}
	
	public void setDate(String d){date = d;}
	
	public Action(){

		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		 date = sdf.format(new Date());
	}
	
	public String getDescription(){return "";}
	
	@Override
	public String toString(){
		return " : " + getDescription();
//		return time + " : " + getDescription();
	}
	

}
