package SecurityTest;

import SecurityTest.SecurityGroup;
import SecurityTest.SecurityUser;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class SecurityUserEJB {

    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;

    public SecurityUser createUser(SecurityUser user) {
        try {
            user.setPassword(AuthenticationUtility.encodeSHA256(user.getPassword()));
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
        SecurityGroup group = new SecurityGroup();
        group.setUsername(user.getUsername());
        group.setGroupname(SecurityGroup.USERS_GROUP);
        em.persist(user);
        em.persist(group);
        return user;
    }
    public SecurityUser findUserById(String id) {
        TypedQuery<SecurityUser> query = em.createNamedQuery("findUserById", SecurityUser.class);
        query.setParameter("username", id);
        SecurityUser user = null;
        try {
            user = query.getSingleResult();
        } catch (Exception e) {
            // getSingleResult throws NoResultException in case there is no user in DB
            // ignore exception and return NULL for user instead
        }
        return user;
    }
}
