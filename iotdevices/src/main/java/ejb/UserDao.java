package ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

import entities.Device;
import entities.Subscription;
import entities.User;

@Stateless
public class UserDao {
    // Injected database connection:
    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;

    // Stores a new tweet:
    public void persist(User user) {
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
}