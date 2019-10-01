package entities;

import javax.jws.soap.SOAPBinding;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "device_subscriber")
public class Subscription {


    // @EmbeddedId
    // private DeviceSubscriberID deviceSubscriberID;

    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "yourTableGenerator")
    private int id;

    @ManyToOne
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    private boolean isApprovedSubscription = false;

    public Subscription() {
    }

    public Subscription(Device device, User user) {
        this.device = device;
        this.user = user;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isApprovedSubscription() {
        return isApprovedSubscription;
    }
}
