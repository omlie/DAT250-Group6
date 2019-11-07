package rest.models;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeviceModificationRequest {
    @XmlElement public int deviceid;
    @XmlElement public int userid;
}

