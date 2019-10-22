package ejb;

import entities.Device;
import entities.Subscription;
import entities.User;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Named(value = "subscriptionController")
@RequestScoped
public class SubscriptionController implements Serializable {

    private static final long serialVersionUID = 1L;

    // Injected DAO EJB:
    @EJB
    private UserDao userDao;

    @EJB
    private DeviceDao deviceDao;

    @EJB
    private SubscriptionDao subDao;

    private User user;
    private Device device;

    public List<User> pendingSubscribers(int deviceId) {
        List<User> pendingUsers = new ArrayList<>();
        device = deviceDao.getDeviceById(deviceId);
        for (Subscription sub : device.getSubscriptions()) {
            if (!sub.isApprovedSubscription())
                pendingUsers.add(sub.getUser());

        }

        return pendingUsers;
    }

    public boolean hasPendingSubscribers(int deviceId) {
        device = deviceDao.getDeviceById(deviceId);
        for (Subscription sub : device.getSubscriptions())
            if (!sub.isApprovedSubscription())
                return true;
        return false;
    }


    public void approveSubscriber(int deviceId, int userId) {
        device = deviceDao.getDeviceById(deviceId);
        for (Subscription sub : device.getSubscriptions())
            if (sub.getUser().getId() == userId) {
                subDao.approveSubscription(sub);
                return;
            }
    }

    public String pendingSubscriberStatus(int deviceId) {
        device = deviceDao.getDeviceById(deviceId);
        int numberOfPending = device.getSubscriptions()
                .stream().filter(sub -> !sub.isApprovedSubscription())
                .collect(Collectors.toList()).size();

        String status = numberOfPending == 1 ? " pending subscriber" : " pending subscribers";
        return numberOfPending + status;
    }


}
