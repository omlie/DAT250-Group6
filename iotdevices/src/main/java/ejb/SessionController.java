package ejb;

import entities.User;
import helpers.Constants;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

@Named(value = "sessionController")
@SessionScoped
public class SessionController implements Serializable {

    private static final long serialVersionUID = 1L;

    private String password;
    private String username;

    @EJB
    private UserDao userDao;

    public String login(){
        try {
            if(userDao.checkPassword(username, password)) {
                HttpSession session = SessionUtil.getSession();
                session.setAttribute(Constants.USERNAME, this.username);
                return Constants.MYPAGE;
            }
        } catch (Exception ignored){
            return Constants.LOGIN;
        }
        return Constants.MYPAGE;
    }

    public String logout(){
        HttpSession session = SessionUtil.getSession();
        session.invalidate();
        return Constants.LOGIN;
    }

    public String redirect() throws IOException {
        HttpSession session = SessionUtil.getSession();
        if (session.getAttribute(Constants.USERNAME)==null) {
            SessionUtil.getResponse().sendRedirect(Constants.LOGIN + ".xhtml");
        }
        return Constants.MYPAGE;
    }

    public User getUser(){
        return userDao.getUser(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
