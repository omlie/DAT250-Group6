package entities;

import helpers.Status;

import java.io.Serializable;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

@Entity
@Table(name="devices")
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

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonbTransient
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "device_subscribers",
            joinColumns = {@JoinColumn(name = "device_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private List<User> subscribers;

    @JsonbTransient
    @OneToMany
    @JoinColumn(name="device_id")
    private List<Feedback> feedback;

    @JsonbTransient
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "device_labels",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private List<Label> labels;

    public Device() {
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void addLabel(Label label){
        labels.add(label);
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

    public void addSubscriber(User user) {
        subscribers.add(user);
        user.getSubscribedDevices().add(this);
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Feedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<Feedback> feedback) {
        this.feedback = feedback;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}