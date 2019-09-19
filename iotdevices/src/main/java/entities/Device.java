package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name="DEVICE")
@NamedQuery(name="Device.findAll", query="SELECT d FROM Device d")
public class Device implements  Serializable {
    public static final String FIND_ALL = "Device.findAll";
    private static final long serialVersionUID = 1L;
    //Create elements ids automatically, incremented 1 by 1
    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE,generator="yourTableGenerator")
    private int id;

    private String deviceName;

    private String deviceImg;

    private String apiUrl;

    private int status;

    @OneToMany(targetEntity = User.class)
    private List<User> subscribers;




    public Device() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    public String getDeviceImg() {
        return deviceImg;
    }

    public void setDeviceImg(String deviceImg) {
        this.deviceImg = deviceImg;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}