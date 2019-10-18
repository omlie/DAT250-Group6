package ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;

import entities.Device;
import entities.Subscription;
import entities.User;
import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class UserDao {
    // Injected database connection:
    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;

    // Stores a new tweet:
    public void persist(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        em.persist(user);
    }

    public void merge(User user) {
        em.merge(user);
    }

    // Retrieves all the tweets:
    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        Query query = em.createQuery("SELECT u FROM User u");
        List<User> users = new ArrayList<User>();
        users = query.getResultList();
        return users;
    }

    public List<Device> getSubscribedDevices(int userId) {
        User user = em.find(User.class, userId);
        if (user == null)
            throw new NotFoundException();

        Set<Subscription> subscriptions = user.getSubscriptions();

        return subscriptions.stream()
                .map(Subscription::getDevice)
                .collect(Collectors.toList());
    }

    public User deleteOwned(int userid, int deviceId) {
        User user = em.find(User.class, userid);
        Device d = em.find(Device.class, deviceId);
        user.getOwnedDevices().remove(d);
        em.createQuery("DELETE FROM Device d WHERE d.id=?1")
                .setParameter(1, deviceId).executeUpdate();
        merge(user);
        return user;
    }

    public User getUser(int idInt) {
        User user = em.find(User.class, idInt);
        if (user == null)
            throw new NotFoundException();
        return user;
    }

    public User getUser(String username) {
        TypedQuery<User> q = em.createQuery("select user from User user where user.userName=?1", User.class);
        q.setParameter(1, username);
        List<User> users = q.getResultList();
        if (users.isEmpty())
            throw new NotFoundException();
        return users.get(0);
    }



    public boolean checkPassword(String username, String password) {
        TypedQuery<User> q = em.createQuery("SELECT user from User user WHERE user.userName=?1", User.class);
        q.setParameter(1, username);
        List<User> users = q.getResultList();
        if(users.isEmpty())
            return false;
        return BCrypt.checkpw(password, users.get(0).getPassword());
    }

    public List<Device> getOwnedDevices(int idInt) {
        User user = em.find(User.class, idInt);
        if (user == null)
            throw new NotFoundException();
        return user.getOwnedDevices();
    }


    public User unsubscribe(String username, int deviceId) {
        List<Subscription> q =
                em.createQuery("select s from Subscription s where s.device.id=?1 and s.user.userName=?2", Subscription.class)
                .setParameter(1, deviceId)
                .setParameter(2, username)
                .getResultList();

        // Id result is empty or more than one, there is something wrong
        if(q.size() != 1)
            throw new NotFoundException();

        Subscription s = q.get(0);
        User u = s.getUser();
        u.getSubscriptions().remove(s);
        em.createQuery("DELETE FROM Subscription s WHERE s.id=?1")
                .setParameter(1, s.getId()).executeUpdate();
        return u;
    }
}