package entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    private String userName;
    private String password;

    @Id
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
