package SecurityTest;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NamedQueries({
        @NamedQuery(name = "findUserById", query = "SELECT u FROM SecurityUser u WHERE u.username = :username")
})
@Table(name = "securityuser")
public class SecurityUser implements Serializable {
    private static final long serialVersionUID = -5892169641074303723L;

    @Id
    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    public SecurityUser() {
    }

    public SecurityUser(String username, String password, String name) {
        this.username = username;
        this.password = password;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

