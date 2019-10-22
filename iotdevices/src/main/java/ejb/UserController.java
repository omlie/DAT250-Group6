package ejb;

import entities.Device;
import entities.Label;
import entities.Subscription;
import entities.User;
import helpers.Constants;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named(value = "userController")
@RequestScoped
public class UserController implements Serializable {

    private static final long serialVersionUID = 1L;

    // Injected DAO EJB:
    @EJB
    private UserDao userDao;

    private User user;
    private Device device;

    private Label l1;
    private Label l2;
    private Label l3;

    public List<User> getUsers() {
        List<User> reverseDeviceList = new ArrayList<User>();
        reverseDeviceList.addAll(this.userDao.getAllUsers());
        Collections.reverse(reverseDeviceList);
        return reverseDeviceList;
    }

    public List<Device> subscribedTo(){
        List<Device> subscribedTo = new ArrayList<>();
        for(Subscription s : userDao.getUser(getUsername()).getSubscriptions()){
            subscribedTo.add(s.getDevice());
        }
        return subscribedTo;
    }

    public String unsubscribe(int deviceid){
        try {
            userDao.unsubscribe(getUsername(), deviceid);
        } catch (Exception e) {
            // redirect
            return "404";
        }
        return "feedback";
    }

    public String addSubscription(int deviceid, String username){
        try {
            userDao.addSubscriber(deviceid, username);
        } catch (Exception e) {
            // redirect
            return Constants.ERROR;
        }
        return Constants.MYPAGE;
    }

    public String deleteOwned(int deviceId){
        try {
            userDao.deleteOwned(getUsername(), deviceId);
        } catch (Exception e) {
            //redirect
            return Constants.ERROR;
        }
        return Constants.MYPAGE;
    }

    public String addLabels(int deviceid, List<Label> labels){
        try {
            userDao.addLabels(deviceid, labels);
        } catch (Exception e) {
            return Constants.ERROR;
        }
        return Constants.DEVICE;
    }

    public String updateUser(){
        try {
            userDao.updateUser(user);
        } catch (Exception ignored) {
        }
        return Constants.MYPAGE;
    }

    public String editOwned(Device device){
        try {
            if(device != null) {
                userDao.editOwned(device);
            }
        } catch (Exception e) {
            return Constants.ERROR;
        }
        return Constants.DEVICE;
    }

    public String publishDevice(int deviceid){
        try {
            userDao.publishDevice(deviceid);
        } catch (Exception e) {
            return Constants.ERROR;
        }
        return Constants.DEVICE;
    }

    public String addDevice(Device device){
        try {
            if(device != null) {
                // Add and reset labels
                if(l1 != null)
                    device.addLabel(l1);
                if(l2 != null)
                    device.addLabel(l2);
                if(l3 != null)
                    device.addLabel(l3);

                userDao.addDevice(getUsername(), device);
                this.device = null;
                l1 = null;
                l2 = null;
                l3 = null;
            }
        } catch (Exception e) {
            // redirect
            return Constants.ERROR;
        }
        return "mypage";
    }

    public String saveUser() {
        try {
            List<User> users = this.userDao.getAllUsers();
            for(User u : users){
                if(u.getUserName().equals(this.user.getUserName())){
                    return Constants.LOGIN;
                }
            }
            this.userDao.persist(this.user);
        } catch (Exception e) {
            // redirect
            return Constants.ERROR;
        }
        return Constants.LOGIN;
    }

    public User getUser() {
        if (this.user == null) {
            user = new User();
        }
        return user;
    }

    public Device getDevice() {
        if (this.device == null) {
            device = new Device();
        }
        return device;
    }

    public Label getL1() {
        l1 = new Label();
        return l1;
    }

    public Label getL2() {
        l2 = new Label();
        return l2;
    }

    public Label getL3() {
        l3 = new Label();
        return l3;
    }

    public String getUsername(){
        return (String)SessionUtil.getSession().getAttribute(Constants.USERNAME);
    }

    public boolean isOwner(int deviceId) {
        for (Device dev : userDao.getUser(getUsername()).getOwnedDevices())
            if (dev.getId() == deviceId)
                return true;

        return false;
    }

    public boolean isSubscribedTo(int deviceId) {
        if(isOwner(deviceId)) return false;
        for (Device dev : this.subscribedTo())
            if (dev.getId() == deviceId)
                return true;

        return false;
    }

    public boolean hasConnectionTo(int deviceId) {
        return (isOwner(deviceId) || isSubscribedTo(deviceId));
    }
}
