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

import entities.*;
import helpers.Status;

@Stateless
public class DeviceDao {
    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;

    public void persist(Device device) {
        em.persist(device);
    }

    @SuppressWarnings("unchecked")
    /**
     * Get all devices that exists in the db
     */
    public List<Device> getAllDevices() {
        Query query = em.createQuery("SELECT d FROM Device d");
        return query.getResultList();
    }

    /**
     * Get the device with the given id
     * @param deviceId
     * @return device
     */
    public Device getDeviceById(int deviceId) {
        Device device = em.find(Device.class, deviceId);
        if (device == null)
            throw new NotFoundException();
        return device;
    }

    /**
     * Get all subscribers to a given device
     * @param deviceId
     * @return list of users
     */
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

    /**
     * Get the information related to a given subscription
     * @param deviceId
     * @param subscriptionId
     * @return
     */
    public Subscription getSubscriptionInfo(int deviceId, int subscriptionId) {
        Device device = getDeviceById(deviceId);
        Set<Subscription> subscribers = device.getSubscriptions();

        for(Subscription aSubscription : subscribers) {
            if (aSubscription.getId() == subscriptionId)
                return aSubscription;
        }
        throw new NotFoundException();
    }

    /**
     * Get all feedback that are given to a device
     * @param deviceId
     * @return list of feedback
     */
    public List<Feedback> getFeedback(int deviceId) {
        Device device = em.find(Device.class, deviceId);
        if (device == null)
            throw new NotFoundException();
        return device.getFeedback();
    }

    /**
     * Get all registered online devices with a given label
     * @param label
     * @return list of devices
     */
    public List<Device> filterDevicesByLabel(String label) {
        TypedQuery<Device> query = em.createNamedQuery(Label.FIND_BY_NAME, Device.class);
        query.setParameter("name", label);
        List<Device> devices = query.getResultList();

        if (devices == null)
            throw new NotFoundException();

        // Only display online devices
        return getOnlineDevices(devices);
    }

    /**
     * Get all devices that are online
     * @param devices
     * @return all devices that are online
     */
    private List<Device> getOnlineDevices(List<Device> devices){
        List<Device> online = new ArrayList<>();
        for(Device d : devices){
            if(d == null || d.getStatus() == null)
                continue;
            if(d.getStatus() == Status.ONLINE)
                online.add(d);
        };
        return online;
    }

    /**
     * get all devices connected to a given label id
     * @param labelId
     * @return list of devices
     */
    public List<Device> filterDevicesByLabelId(int labelId) {
        Label foundLabel = em.find(Label.class, labelId);
        List<Device> devices = foundLabel.getDevices();
        if (devices == null)
            throw new NotFoundException();
        return getOnlineDevices(devices);
    }

    public void addFeedback(Device device, Feedback feedback) {
        device.addFeedback(feedback);
        em.merge(device);
    }
}