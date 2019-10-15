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

    public List<User> getUsers() {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_ALL, User.class);
        return query.getResultList();
    }

    public User getUser(int idInt) {
        User user = em.find(User.class, idInt);
        if (user == null)
            throw new NotFoundException();
        return user;
    }

    public List<Device> getOwnedDevices(int idInt) {
        User user = em.find(User.class, idInt);
        if (user == null)
            throw new NotFoundException();
        return user.getOwnedDevices();
    }
}