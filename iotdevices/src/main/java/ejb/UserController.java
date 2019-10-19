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

    public String unsubscribe(int deviceid){
        this.user = userDao.unsubscribe(user.getId(), deviceid);
        return "mypage";
    }

    public String addSubscription(int deviceid, int userid){
        userDao.addSubscriber(deviceid, userid);
        this.user = userDao.getUser(userid);
        return "mypage";
    }


    public String userLogin() {
        if (userDao.checkPassword(user.getUserName(), user.getPassword())) {
            user = userDao.getUser(user.getUserName());
            return "mypage";
        }
        return "index";
    }

    public String deleteOwned(int deviceId){
        userDao.deleteOwned(user.getId(), deviceId);
        this.user = userDao.getUser(user.getId());
        return "mypage";
    }

    public void editOwned(Device device){
        if(device != null) {
            userDao.editOwned(device);
            user.setOwnedDevices(userDao.getOwnedDevices(user.getId()));
        }
    }

    public void publishDevice(int deviceid){
        if(userDao.publishDevice(deviceid))
            this.user = userDao.getUser(user.getId());
    }

    public String addDevice(Device device){
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
        return "mypage";
    }

    public String saveUser() {
        List<User> users = this.userDao.getAllUsers();
        for(User u : users){
            if(u.getUserName().equals(this.user.getUserName())){
                return "users";
            }
        }
        this.userDao.persist(this.user);
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
