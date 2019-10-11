package ejb;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named(value = "feedbackController")
@RequestScoped
public class FeedbackController {


    private String feedback;
    private String inputedString;

    public void submitFeedback() {
        // TODO: Actually do someting with the submitted feedback
        String result = "Submitted feedback: " + feedback;
        System.out.println(result);
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
