package rest.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class DeviceAddRequest{
    @XmlElement public String devicename;
    @XmlElement public String deviceimg;
    @XmlElement public String apiurl;
    @XmlElement public List<String> labels;
    @XmlElement public Integer status;
    @XmlElement public Integer ownerId;
}
