package entities;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="Feedback")
// @NamedQuery(name="Feedback.findAll", query="SELECT d FROM Feedback d")
public class Feedback implements Serializable {
    public static final String FIND_ALL = "Feedback.findAll";
    private static final long serialVersionUID = 1L;

    //Create elements ids automatically, incremented 1 by 1
    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE,generator="yourTableGenerator")
    private int id;

    private String publishedDate;

    private String feedbackContent;

    @ManyToOne()
    @JoinColumn(name= "user_id")
    private User author;

    @JsonbTransient
    @ManyToOne
    @JoinColumn(name= "device_id")
    private Device device;

    public Feedback() {
    }

    public Feedback(User author, Device device, String feedbackContent, String publishedDate) {
        this.author = author;
        this.device = device;
        this.feedbackContent = feedbackContent;
        this.publishedDate = publishedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}