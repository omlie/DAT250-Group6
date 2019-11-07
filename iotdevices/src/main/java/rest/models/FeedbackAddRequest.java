package rest.models;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FeedbackAddRequest{
    @XmlElement public int deviceid;
    @XmlElement public int userid;
    @XmlElement public String feedback;
}

