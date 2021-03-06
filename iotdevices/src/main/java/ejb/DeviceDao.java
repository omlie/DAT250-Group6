package ejb;

import entities.*;
import helpers.Status;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void addFeedback(Feedback f){
        em.persist(f);
        Device device = f.getDevice();
        device.addFeedback(f);
        em.merge(device);
    }

    public void createDevice(Device d){
        User user = d.getOwner();
        user.addOwnedDevice(d);
        em.persist(d);
        em.merge(user);
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

    public List<User> getApprovedSubscribers(int deviceId) {
        Device device = em.find(Device.class, deviceId);
        if (device == null)
            throw new NotFoundException();

        Set<Subscription> subscriptions = device.getSubscriptions();

        return subscriptions
                .stream()
                .filter(Subscription::isApprovedSubscription)
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
    public List<Device> getOnlineDevices(List<Device> devices){
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
     * Add a label to db if it is not there before
     * @param labelValue
     * @return the label connected to the labelvalue
     */
    public Label addNewLabel(String labelValue){
        List<Label> labels =
                em.createQuery("select l from Label l where l.labelValue=?1", Label.class)
                        .setParameter(1, labelValue).getResultList();
        if(labels.isEmpty()){
            Label l = new Label();
            l.setLabelValue(labelValue);
            em.persist(l);
            return l;
        } else {
            return labels.get(0);
        }
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

    public void saveEditedDevice(Device device) {
        em.merge(device);
    }
}