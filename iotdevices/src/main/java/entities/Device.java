package entities;

import helpers.Status;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "devices")
@NamedQuery(name = "Device.findAll", query = "SELECT d FROM Device d")
public class Device implements Serializable, Comparable<Device>  {
    public static final String FIND_ALL = "Device.findAll";
    private static final long serialVersionUID = 1L;
    //Create elements ids automatically, incremented 1 by 1
    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "yourTableGenerator")
    private int id;

    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    private String deviceName;

    private String deviceImg;

    private String apiUrl;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonbTransient
    @OneToMany(mappedBy = "device", cascade = {CascadeType.ALL})
    private List<Feedback> feedback;

    @JsonbTransient
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "device_labels",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private List<Label> labels = new ArrayList<>();

    @JsonbTransient
    @OneToMany(mappedBy = "device", cascade = {CascadeType.ALL})
    private Set<Subscription> subscriptions = new HashSet<>();


    public Device() {
    }

    public Status[] getStatuses() {
        return Status.values();
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public void addLabel(Label label) {
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

    public void addSubscriber(Subscription subscription) {
        subscriptions.add(subscription);
        subscription.getUser().getSubscriptions().add(subscription);
    }

    public void addFeedback(Feedback newFeedback) {
        feedback.add(newFeedback);
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

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }



    @Override
    public int compareTo(Device o) {
        return this.getId() - o.getId();
    }
}