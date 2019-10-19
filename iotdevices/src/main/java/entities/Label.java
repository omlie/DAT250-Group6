package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="labels")
@NamedQuery(name="Label.findAll", query="SELECT l FROM Label l")
@NamedQuery(name = "Label.findByName", query ="SELECT l.devices FROM Label l WHERE l.labelValue = :name" )
public class Label implements Serializable {
    public static final String FIND_ALL = "Label.findAll";
    public static final String FIND_BY_NAME = "Label.findByName";
    private static final long serialVersionUID = 1L;

    //Create elements ids automatically, incremented 1 by 1
    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE,generator="yourTableGenerator")
    private int id;

    private String labelValue;

    @ManyToMany(mappedBy = "labels")
    private List<Device> devices;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabelValue() {
        return labelValue;
    }

    public void setLabelValue(String value) {
        this.labelValue = value;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public void addDevice(Device device){
        this.devices.add(device);
    }

    public Label() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return Objects.equals(labelValue, label.labelValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labelValue);
    }

}