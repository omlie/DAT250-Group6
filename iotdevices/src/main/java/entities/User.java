package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="IOTUSER")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String FIND_ALL = "User.findAll";

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

    @OneToMany( targetEntity = Device.class)
    private List<Device> ownedDevices;

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

}