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

    /**
     * Applies the changes to apiurl, deviceimage and devicename to the
     * given device
     * @param device
     * @return the edited device
     */
    public void editOwned(Device device){
        Device d = em.find(Device.class, device.getId());
        if(d == null)
            throw new NotFoundException();

        d.setApiUrl(device.getApiUrl());
        d.setDeviceImg(device.getDeviceImg());
        d.setDeviceName(device.getDeviceName());
        em.merge(d);
    }

    /**
     * Get all devices that a user is subscribed to
     * @param userId
     * @return the list of devices the user is subscribed to
     */
    public List<Device> getSubscribedDevices(int userId) {
        User user = em.find(User.class, userId);
        if (user == null)
            throw new NotFoundException();

        Set<Subscription> subscriptions = user.getSubscriptions();

        return subscriptions.stream()
                .map(Subscription::getDevice)
                .collect(Collectors.toList());
    }

    /**
     * Delete one of a users owned devices
     * @param userid
     * @param deviceId
     * @return
     */
    public void deleteOwned(int userid, int deviceId) {
        User user = em.find(User.class, userid);
        Device d = em.find(Device.class, deviceId);
        user.getOwnedDevices().remove(d);
        em.createQuery("DELETE FROM Device d WHERE d.id=?1")
                .setParameter(1, deviceId).executeUpdate();
        merge(user);
    }

    /**
     * Get a user with the given id
     * @param idInt
     * @return user
     * @throws NotFoundException
     */
    public User getUser(int idInt) {
        User user = em.find(User.class, idInt);
        if (user == null)
            throw new NotFoundException();
        return user;
    }

    /**
     * Get a user with the given username
     * @param username
     * @return a user
     * @throws NotFoundException
     */
    public User getUser(String username) {
        TypedQuery<User> q = em.createQuery("select user from User user where user.userName=?1", User.class);
        q.setParameter(1, username);
        List<User> users = q.getResultList();
        if (users.isEmpty())
            throw new NotFoundException();
        return users.get(0);
    }

    /**
     * Check that the password for a given user is OK
     * @param username
     * @param password
     * @return
     */
    public boolean checkPassword(String username, String password) {
        TypedQuery<User> q = em.createQuery("SELECT user from User user WHERE user.userName=?1", User.class);
        q.setParameter(1, username);
        List<User> users = q.getResultList();
        if(users.isEmpty())
            return false;
        return BCrypt.checkpw(password, users.get(0).getPassword());
    }

    /**
     * Get all devices that the given user owns
     * @param idInt
     * @return The list of all devices
     */
    public List<Device> getOwnedDevices(int idInt) {
        User user = em.find(User.class, idInt);
        if (user == null)
            throw new NotFoundException();
        return user.getOwnedDevices();
    }

    /**
     * Add a device from a specific user. The device will
     * be a owned device for the user
     * @param userid
     * @param device
     * @return
     */
    public User addDevice(int userid, Device device){
        User user = em.find(User.class, userid);
        user.addOwnedDevice(device);
        em.persist(device);
        em.merge(user);
        return user;
    }

    /**
     * Add the given user as a subscriber to a device
     * @param deviceId
     * @param userId
     */
    public void addSubscriber(int deviceId, int userId) {
        Device device = em.find(Device.class, deviceId);
        User user = em.find(User.class, userId);
        if (device == null || user == null)
            throw new NotFoundException(deviceId + ", " + userId);

        // Create subscription
        Subscription subscription = new Subscription();
        subscription.setDevice(device);
        subscription.setUser(user);

        // Already a subscription-relation
        if(user.getSubscriptions().contains(subscription))
            return;

        device.addSubscriber(user);
        user.addSubscriber(device);
        em.persist(device);
    }

    /**
     * Unsubscribe a given user from a given device
     * @param userid
     * @param deviceId
     * @return
     */
    public User unsubscribe(int userid, int deviceId) {
        List<Subscription> q =
                em.createQuery("select s from Subscription s where s.device.id=?1 and s.user.id=?2", Subscription.class)
                .setParameter(1, deviceId)
                .setParameter(2, userid)
                .getResultList();

        // If result is empty. If there are more than one subscription to the same device, just remove one
        if(q.size() == 0)
            throw new NotFoundException();

        Subscription s = q.get(0);
        s.getUser().getSubscriptions().remove(s);
        em.createQuery("DELETE FROM Subscription s WHERE s.id=?1")
                .setParameter(1, s.getId()).executeUpdate();

        return em.find(User.class, userid);
    }
}