package SecurityTest;

import entities.User;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;

@ManagedBean
@SessionScoped
@Named("registerView")
public class RegisterView implements Serializable {
    private static final long serialVersionUID = 1685823449195612778L;
    private static Logger log = Logger.getLogger(RegisterView.class.getName());

    @EJB
    private SecurityUserEJB userEJB;
    private String name;
    private String username;
    private String password;
    private String confirmPassword;

    public String register() {
        SecurityUser user = new SecurityUser(username, password, name);
        userEJB.createUser(user);
        log.info("New user created with e-mail: " + username + " and name: " + name);
        return "signin";
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}