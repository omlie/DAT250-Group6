package entities;

import org.mindrot.jbcrypt.BCrypt;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
public class User implements Serializable {
    public static final String FIND_ALL = "User.findAll";
    private static final long serialVersionUID = 1L;

    //Create elements ids automatically, incremented 1 by 1
    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "yourTableGenerator")
    private int id;

    @Column(unique = true)
    private String userName;
    private String firstName;
    private String lastName;

    //@JsonbTransient
    private String password;

    @JsonbTransient
    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Device> ownedDevices;

    @JsonbTransient
    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Feedback> feedback;

    @JsonbTransient
    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private Set<Subscription> subscriptions = new HashSet<>();

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

    public List<Feedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<Feedback> feedback) {
        this.feedback = feedback;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return BCrypt.checkpw(password, this.password);
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void addSubscriber(Device device) {
        Subscription newSubscription = new Subscription(device, this);
        // user.getSubscriptions().add(newSubscription);
        this.subscriptions.add(newSubscription);
    }
}