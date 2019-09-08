package entities;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@XmlRootElement
@Table
@NamedQuery(name="Device.findAll", query="SELECT d FROM Device d")
public class Device implements Serializable {
    private static final long serialVersionUID = 1L;

    //Create elements ids automatically, incremented 1 by 1
    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE,generator="yourTableGenerator")
    private int id;

    private String message;

    private String topic;

    private String author;

    public static final String FIND_ALL = "Device.findAll";

    public Device() {
    }

    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Tweet [id=" + id + ", message=" + message + ", topic=" + topic + ", author=" + author + "]";
    }


}