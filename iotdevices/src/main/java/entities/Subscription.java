package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "device_subscriber")
public class Subscription implements Serializable {


    // @EmbeddedId

    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "yourTableGenerator")
    private int id;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isApprovedSubscription = false;

    private boolean isDeniedSubscription = false;

    public Subscription() {
    }

    public Subscription(Device device, User user) {
        this.device = device;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return device.equals(that.device) &&
                user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, user);
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

    public void setApprovedSubscription(boolean approvedSubscription) {
        isApprovedSubscription = approvedSubscription;
    }

    public boolean isDeniedSubscription() {
        return isDeniedSubscription;
    }

    public void setDeniedSubscription(boolean deniedSubscription) {
        isDeniedSubscription = deniedSubscription;
    }
}
