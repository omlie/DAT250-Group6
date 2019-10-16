package ejb;

import entities.Device;
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

    public List<User> getUsers() {
        List<User> reverseDeviceList = new ArrayList<User>();
        reverseDeviceList.addAll(this.userDao.getAllUsers());
        Collections.reverse(reverseDeviceList);
        return reverseDeviceList;
    }


    public String userLogin() {
        if (userDao.checkPassword(user.getUserName(), user.getPassword()))
            return "mypage";
        return "index";
    }

    public String deleteOwned(int deviceId){
        for(Device d : user.getOwnedDevices()){
            if(d.getId() == deviceId){
                user.getOwnedDevices().remove(d);
            }
        }
        userDao.persist(user);
        //TODO: Change to mypage when mypage pr merged
        return "devices";
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

}
