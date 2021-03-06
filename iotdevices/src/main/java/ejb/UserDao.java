package ejb;

import entities.*;
import helpers.AuthenticationUtility;
import helpers.Constants;
import helpers.SessionUtil;
import helpers.Status;
import org.mindrot.jbcrypt.BCrypt;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class UserDao {
    // Injected database connection:
    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;

    public void merge(User user) {
        em.merge(user);
    }

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

    public boolean createUser(User user) {
        try {
            user.setPassword(AuthenticationUtility.encodeSHA256(user.getPassword()));
        } catch (Exception ignored) {
            return false;
        }
        SecurityGroup group = new SecurityGroup();
        group.setUsername(user.getUserName());
        group.setGroupname(SecurityGroup.USERS_GROUP);
        em.persist(user);
        em.persist(group);
        return true;
    }

    public boolean login(String username, String password) {
        HttpServletRequest request = SessionUtil.getRequest();
        try {
            request.login(username, password);
            return true;
        } catch (ServletException e) {
            return false;
        }
    }

    public boolean login(User user, String username, String plaintext) {
        return user.getUserName().equals(username) && BCrypt.checkpw(plaintext, user.getPassword());
    }

    /**
     * Delete a user from the database
     *
     * @param user
     */
    public void deleteUser(User user) {
        for (Subscription s : user.getSubscriptions()) {
            unsubscribe(user.getId(), s.getDevice().getId());
        }
        for (Feedback f : user.getFeedback()) {
            f.setAuthor(null);
            em.merge(f);
        }
        em.createQuery("DELETE from User u WHERE u.id=?1", User.class)
                .setParameter(1, user.getId())
                .executeUpdate();
    }

    public void makeAdmin(User user) {
        SecurityGroup group = em.find(SecurityGroup.class, user.getUserName());
        if (group == null)
            throw new NotFoundException();
        group.setGroupname(SecurityGroup.ADMINS_GROUP);
        em.merge(group);
    }

    public boolean isAdmin(String username) {
        SecurityGroup group = em.find(SecurityGroup.class, username);
        if (group == null)
            return false;
        return group.getGroupname().equals(SecurityGroup.ADMINS_GROUP);
    }

    /**
     * Applies the changes to apiurl, deviceimage and devicename to the
     * given device
     *
     * @param device
     * @return the edited device
     */
    public void editOwned(Device device) {
        Device d = em.find(Device.class, device.getId());
        if (d == null)
            throw new NotFoundException();

        d.setApiUrl(device.getApiUrl());
        d.setDeviceImg(device.getDeviceImg());
        d.setDeviceName(device.getDeviceName());
        d.setStatus(device.getStatus());
        em.merge(d);
    }

    /**
     * Get all devices that a user is subscribed to
     *
     * @param userid
     * @return the list of devices the user is subscribed to
     */
    public List<Device> getSubscribedDevices(int userid) {
        return getSubscribedDevices(em.find(User.class, userid));
    }

    /**
     * Get all devices that a user is subscribed to
     *
     * @param username
     * @return the list of devices the user is subscribed to
     */
    public List<Device> getSubscribedDevices(String username) {
        return getSubscribedDevices(getUser(username));
    }

    private List<Device> getSubscribedDevices(User user) {
        if (user == null)
            throw new NotFoundException();

        Set<Subscription> subscriptions = user.getSubscriptions();

        return subscriptions.stream()
                .filter(Subscription::isApprovedSubscription)
                .map(Subscription::getDevice)
                .collect(Collectors.toList());
    }

    /**
     * Delete one of a users owned devices
     *
     * @param userid
     * @param deviceId
     * @return
     */
    public void deleteOwned(int userid, int deviceId) {
        User user = getUser(userid);
        Device d = em.find(Device.class, deviceId);
        if (d == null || user == null)
            throw new NotFoundException();


        em.createQuery("DELETE FROM Subscription s WHERE s.device.id =?1")
                .setParameter(1, deviceId)
                .executeUpdate();
        em.createQuery("DELETE FROM Feedback f WHERE f.device.id=?1")
                .setParameter(1, deviceId)
                .executeUpdate();
        em.createQuery("DELETE FROM Device d WHERE d.id=?1")
                .setParameter(1, deviceId)
                .executeUpdate();

        user.getOwnedDevices().remove(d);
        em.merge(user);
    }

    public boolean userExists(String username) {
        TypedQuery<User> q = em.createQuery("select user from User user where user.userName=?1", User.class);
        q.setParameter(1, username);
        List<User> users = q.getResultList();
        return !users.isEmpty();
    }

    /**
     * Get a user with the given id
     *
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
     * Change status of a device from offline to online
     *
     * @param deviceid
     * @return true if success false else
     */
    public boolean publishDevice(int deviceid) {
        Device d = em.find(Device.class, deviceid);
        if (d == null)
            return false;
        d.setStatus(Status.ONLINE);
        return true;
    }

    /**
     * Get a user with the given username
     *
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
     * Get all devices that the given user owns
     *
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
     * Add new labels to a device
     *
     * @param deviceid
     * @param labels,  labels to be added
     */
    public void addLabels(int deviceid, List<Label> labels) {
        Device d = em.find(Device.class, deviceid);
        List<Label> devicelabels = new ArrayList<>();
        for (Label l : labels) {
            if (l != null && !l.getLabelValue().equals("")) {
                devicelabels.add(addLabel(l.getLabelValue()));
            }
        }
        if (!devicelabels.isEmpty()) {
            for (Label l : devicelabels) {
                d.addLabel(l);
            }
        }
        em.merge(d);
    }


    private Label addLabel(String labelvalue) {
        Label l;
        List<Label> labels =
                em.createQuery("select l from Label l where l.labelValue=?1", Label.class)
                        .setParameter(1, labelvalue).getResultList();

        // This is a new label
        if (labels.isEmpty()) {
            l = new Label();
            l.setLabelValue(labelvalue);
            em.persist(l);
        }
        // This label exist, use the existing
        else {
            l = labels.get(0);
        }
        return l;
    }

    /**
     * Add a device from a specific user. The device will
     * be a owned device for the user
     *
     * @param username
     * @param device
     */
    public void addDevice(String username, Device device) {
        User user = getUser(username);
        device.setOwner(user);
        if (user == null)
            throw new NotFoundException();

        List<Label> deviceLabels = new ArrayList<>();
        for (Label l : device.getLabels()) {
            if (l != null && !l.getLabelValue().equals(""))
                deviceLabels.add(addLabel(l.getLabelValue()));
        }

        device.setLabels(deviceLabels);
        user.addOwnedDevice(device);
        em.persist(device);
        em.merge(user);
    }


    public void addSubscriber(int deviceId, int userid) {
        addSubscriber(em.find(Device.class, deviceId), em.find(User.class, userid));
    }

    /**
     * Add the given user as a subscriber to a device
     *
     * @param device
     * @param user
     */
    public void addSubscriber(Device device, User user) {
        if (device == null || user == null)
            throw new NotFoundException();

        Subscription subscription = new Subscription(device, user);

        // Already a subscription-relation
        device.addSubscriber(subscription);
        if (user.getSubscriptions().contains(subscription))
            return;
        em.persist(subscription);
        em.merge(device);
        em.merge(user);
    }

    public void updateUser(User user) {
        User stored = em.find(User.class, user.getId());
        if (stored == null)
            throw new NotFoundException();
        stored.setFirstName(user.getFirstName());
        stored.setLastName(user.getLastName());
        merge(stored);
    }

    /**
     * Unsubscribe a given user from a given device
     *
     * @param userid
     * @param deviceId
     */
    public void unsubscribe(int userid, int deviceId) {
        List<Subscription> q =
                em.createQuery("select s from Subscription s where s.device.id=?1 and s.user.id=?2", Subscription.class)
                        .setParameter(1, deviceId)
                        .setParameter(2, userid)
                        .getResultList();

        // If result is empty. If there are more than one subscription to the same device, just remove one
        if (q.size() == 0)
            throw new NotFoundException();

        Subscription s = q.get(0);
        s.getUser().getSubscriptions().remove(s);
        em.createQuery("DELETE FROM Subscription s WHERE s.id=?1")
                .setParameter(1, s.getId()).executeUpdate();
        em.merge(em.find(User.class, userid));
        em.merge(em.find(Device.class, deviceId));

    }
}