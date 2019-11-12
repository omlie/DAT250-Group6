package ejb;

import javax.annotation.Resource;

import entities.Device;
import entities.Subscription;
import entities.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSSessionMode;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SubscriptionDao {
    // Injected database connection:
    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;

    @Inject
    @JMSConnectionFactory("jms/dat250/ConnectionFactory")
    @JMSSessionMode(JMSContext.AUTO_ACKNOWLEDGE)
    private JMSContext context;

    @Resource(lookup = "jms/dat250/Topic")
    private Topic topic;

    public void approveSubscription(Subscription subscription) {
        subscription.setApprovedSubscription(true);
        subscription.setDeniedSubscription(false);
        em.merge(subscription);

        context.createProducer().setProperty("topicSubscription", "dweet").send(topic, subscription); // .setProperty("topicUser", tweet.getTopic()).send(topic, tweet);
    }

    public void denySubscription(Subscription subscription) {
        subscription.setDeniedSubscription(true);
        subscription.setApprovedSubscription(false);
        em.merge(subscription);
    }

    public void resetSubscription(Subscription subscription) {
        subscription.setDeniedSubscription(false);
        subscription.setApprovedSubscription(false);
        em.merge(subscription);
    }

    public Subscription getSubscription(int deviceid, int userid) {
        List<Subscription> q =
                em.createQuery("select s from Subscription s where s.device.id=?1 and s.user.id=?2", Subscription.class)
                        .setParameter(1, deviceid)
                        .setParameter(2, userid)
                        .getResultList();
        if (q.isEmpty()) {
            return null;
        }
        return q.get(0);
    }

    public List<Subscription> getPending(int deviceid){
        List<Subscription> q =
                em.createQuery("select s from Subscription s where s.device.id=?1 and s.isApprovedSubscription=false and s.isDeniedSubscription=false", Subscription.class)
                        .setParameter(1, deviceid)
                        .getResultList();
        return q;
    }

    public List<Device> getPendingDevices(int userid){
        List<Subscription> q =
                em.createQuery("select s from Subscription s where s.user.id=?1 and s.isApprovedSubscription=false and s.isDeniedSubscription=false", Subscription.class)
                        .setParameter(1, userid)
                        .getResultList();
        List<Device> d = new ArrayList<>();
        for (Subscription s : q)
            d.add(s.getDevice());
        return d;
    }

    public List<Subscription> getPendingToOwned(int userid){
        User user = em.find(User.class, userid);
        List<Device> owned = user.getOwnedDevices();
        List<Subscription> subs = new ArrayList<>();
        for(Device d : owned) {
            for (Subscription s : d.getSubscriptions()) {
                if(!s.isDeniedSubscription() && !s.isApprovedSubscription()){
                    subs.add(s);
                }
            }
        }
        return subs;
    }
}