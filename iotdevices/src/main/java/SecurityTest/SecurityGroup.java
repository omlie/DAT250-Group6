package SecurityTest;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="security_user_groups")
public class SecurityGroup implements Serializable {
    private static final long serialVersionUID = 1528447384986169065L;

    public static final String USERS_GROUP = "securityusers";

    @Id
    @Column(name="username", nullable=false, length=255)
    private String username;

    @Column(name="groupname", nullable=false, length=32)
    private String groupname;

    public SecurityGroup() {}
    public SecurityGroup(String username, String groupname) {
        this.username = username;
        this.groupname = groupname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupname() {
        return groupname;
    }
    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
}
