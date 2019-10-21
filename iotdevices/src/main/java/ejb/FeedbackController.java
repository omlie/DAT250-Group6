package ejb;

import entities.Device;
import entities.Feedback;
import entities.User;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Named(value = "feedbackController")
@RequestScoped
public class FeedbackController {


    @EJB
    private UserDao userDao;
    @EJB
    private DeviceDao deviceDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public DeviceDao getDeviceDao() {
        return deviceDao;
    }

    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public String getInputtedFeedback() {
        return inputtedFeedback;
    }

    public void setInputtedFeedback(String inputtedFeedback) {
        this.inputtedFeedback = inputtedFeedback;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    private String inputtedFeedback;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    private Device device;
    private User author;

    public String submitFeedback(int deviceId, String username) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String formattedDate = dateFormat.format(date);

        try {
            this.device = deviceDao.getDeviceById(deviceId);
            this.author = userDao.getUser(username);
        } catch (Exception e) {
            // redirect
            return "404";
        }

        Feedback feedback = new Feedback(author, device, inputtedFeedback, formattedDate);

        deviceDao.addFeedback(device, feedback);

        return "devices";
    }

    public String getFeedback() {
        return inputtedFeedback;
    }

    public void setFeedback(String inputedFeedback) {
        this.inputtedFeedback = inputedFeedback;
    }
}
