package ejb;

import entities.User;
import helpers.Constants;
import helpers.SessionUtil;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

@Named(value = "sessionController")
@SessionScoped
public class SessionController implements Serializable {

    private static final long serialVersionUID = 1L;

    private String password;
    private String username;
    private User user;

    @EJB
    private UserDao userDao;

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(username, password);
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed!", null));
            return Constants.LOGIN;
        }

        Principal principal = request.getUserPrincipal();
        this.user = userDao.getUser(principal.getName());
        Map<String, Object> sessionMap = SessionUtil.getSessionMap();
        sessionMap.put(Constants.USER, user);
        SessionUtil.getSession().setAttribute(Constants.USERNAME, this.username);
        if (request.isUserInRole("securityusers")) {
            return "user/" + Constants.MYPAGE;
        } else if (request.isUserInRole("securityadmin")) {
            return "admin/" + Constants.ADMINPAGE;
        } else {
            return Constants.LOGIN;
        }
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = SessionUtil.getRequest();
        try {
            this.user = null;
            request.logout();
            // clear the session
            SessionUtil.getSession().invalidate();
            SessionUtil.getSessionMap().clear();
            return "/index?faces-redirect=true";
        } catch (Exception ignored) {
        }
        return "/index?faces-redirect=true";
    }

    public void redirect(String to) {
        try {
            SessionUtil.getResponse().sendRedirect(SessionUtil.getRequest().getContextPath() + to + ".xhtml");
        } catch (Exception e) {

        }
    }

    public User getUser() {
        return (User) SessionUtil.getSessionMap().get(Constants.USER);
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
