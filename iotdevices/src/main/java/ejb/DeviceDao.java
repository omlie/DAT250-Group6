package ejb;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;

import entities.*;

@Stateless
public class DeviceDao {
    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;

    public void persist(Device device) {
        em.persist(device);
    }

    @SuppressWarnings("unchecked")
    public List<Device> getAllDevices() {
        Query query = em.createQuery("SELECT d FROM Device d");
        return query.getResultList();
    }

    public Device getDeviceById(int deviceId) {
        Device device = em.find(Device.class, deviceId);
        if (device == null)
            throw new NotFoundException();
        return device;
    }

    public List<User> getSubscribers(int deviceId) {
        Device device = em.find(Device.class, deviceId);
        if (device == null)
            throw new NotFoundException();

        Set<Subscription> subscriptions = device.getSubscriptions();

        return subscriptions
                .stream()
                .map(Subscription::getUser)
                .collect(Collectors.toList());
    }

    public Subscription getSubscriptionInfo(int deviceId, int subscriptionId) {
        Device device = getDeviceById(deviceId);
        Set<Subscription> subscribers = device.getSubscriptions();

        for(Subscription aSubscription : subscribers) {
            if (aSubscription.getId() == subscriptionId)
                return aSubscription;
        }
        throw new NotFoundException();
    }

    public List<Feedback> getFeedback(int deviceId) {
        Device device = em.find(Device.class, deviceId);
        if (device == null)
            throw new NotFoundException();
        return device.getFeedback();
    }

    public void addOwner(int userid, Device device){
        User user = em.find(User.class, userid);
        user.addOwnedDevice(device);
        persist(device);
        em.merge(user);
    }


    public void addSubscriber(int deviceId, int userId) {
        Device device = em.find(Device.class, deviceId);
        User user = em.find(User.class, userId);
        if (device == null || user == null)
            throw new NotFoundException();

        // Create subscription
        Subscription subscription = new Subscription();
        subscription.setDevice(device);
        subscription.setUser(user);

        device.addSubscriber(user);
        persist(device);
    }

    public List<Device> filterDevicesByLabel(String label) {
        TypedQuery<Device> query = em.createNamedQuery(Label.FIND_BY_NAME, Device.class);
        query.setParameter("name", label);
        List<Device> devices = query.getResultList();
        if (devices == null)
            throw new NotFoundException();
        return devices;
    }

    public List<Device> filterDevicesByLabelId(int labelId) {
        Label foundLabel = em.find(Label.class, labelId);
        List<Device> devices = foundLabel.getDevices();
        if (devices == null)
            throw new NotFoundException();
        return devices;
    }
}