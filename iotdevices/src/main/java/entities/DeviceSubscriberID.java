package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DeviceSubscriberID implements Serializable {

    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "yourTableGenerator")
    private int id;

    private Device device;

    private User user;

    public DeviceSubscriberID(Device device, User user) {
        this.user = user;
        this.device = device;
    }

    public DeviceSubscriberID() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceSubscriberID that = (DeviceSubscriberID) o;
        return Objects.equals(device, that.device) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {

        return Objects.hash(device, user);
    }
}
