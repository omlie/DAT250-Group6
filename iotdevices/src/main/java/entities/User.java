package entities;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="users")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
    public static final String FIND_ALL = "User.findAll";
    private static final long serialVersionUID = 1L;

    //Create elements ids automatically, incremented 1 by 1
    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE,generator="yourTableGenerator")
    private int id;

    private String userName;
    private String firstName;
    private String lastName;

    @JsonbTransient
    @ManyToMany(mappedBy = "subscribers")
    private List <Device> subscribedDevices;

    @JsonbTransient
    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Device> ownedDevices;

    @JsonbTransient
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Device> getOwnedDevices() {
        return ownedDevices;
    }

    public void setOwnedDevices(List<Device> ownedDevices) {
        this.ownedDevices = ownedDevices;
    }

    public List<Device> getSubscribedDevices() {
        return subscribedDevices;
    }

    public void setSubscribedDevices(List<Device> subscribedDevices) {
        this.subscribedDevices = subscribedDevices;
    }

    public void addSubscribedDevice(Device subscribedDevice) {
        this.subscribedDevices.add(subscribedDevice);
    }
}