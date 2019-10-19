package ejb;

import entities.Device;
import entities.Label;
import entities.Subscription;
import entities.User;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named(value = "userController")
@SessionScoped
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
        for(Subscription s : user.getSubscriptions()){
            subscribedTo.add(s.getDevice());
        }
        return subscribedTo;
    }

    public String unsubscribe(int deviceid){
        try {
            this.user = userDao.unsubscribe(user.getId(), deviceid);
        } catch (Exception e) {
            // redirect
        }
        return "mypage";
    }

    public String addSubscription(int deviceid, int userid){
        try {
            userDao.addSubscriber(deviceid, userid);
            this.user = userDao.getUser(userid);
        } catch (Exception e) {
            // redirect
        }
        return "mypage";
    }


    public String userLogin() {
        try {
            if (userDao.checkPassword(user.getUserName(), user.getPassword())) {
                user = userDao.getUser(user.getUserName());
                return "mypage";
            }
        } catch (Exception e) {
            //redirect
        }
        return "index";
    }

    public String deleteOwned(int deviceId){
        try {
            userDao.deleteOwned(user.getId(), deviceId);
            this.user = userDao.getUser(user.getId());
            return "mypage";
        } catch (Exception e) {
            //redirect
        }
        return "mypage";
    }

    public String addLabels(int deviceid, List<Label> labels){
        try {
            userDao.addLabels(deviceid, labels);
        } catch (Exception e) {
            // redirect
        }
        return "index";
    }

    public String updateUser(){
        try {
            this.user = userDao.updateUser(user);
        } catch (Exception e) {
            // redirect
        }
        return "mypage";
    }

    public String editOwned(Device device){
        try {
            if(device != null) {
                userDao.editOwned(device);
                user.setOwnedDevices(userDao.getOwnedDevices(user.getId()));
            }
        } catch (Exception e) {
            // redirect
        }
        return "index";
    }

    public String publishDevice(int deviceid){
        try {
            if(userDao.publishDevice(deviceid))
                this.user = userDao.getUser(user.getId());
        } catch (Exception e) {
            // redirect
        }
        return "index";
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
                l1 = null;
                l2 = null;
                l3 = null;
                this.user = userDao.addDevice(user.getId(), device);
                this.device = null;
            }
        } catch (Exception e) {
            // redirect
        }
        return "mypage";
    }

    public String saveUser() {
        try {
            List<User> users = this.userDao.getAllUsers();
            for(User u : users){
                if(u.getUserName().equals(this.user.getUserName())){
                    return "users";
                }
            }
            this.userDao.persist(this.user);
        } catch (Exception e) {
            // redirect
        }
        return "mypage";
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
}
