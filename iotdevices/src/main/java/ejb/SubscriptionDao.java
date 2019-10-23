package ejb;

import javax.annotation.Resource;

import entities.Subscription;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSSessionMode;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

        em.merge(subscription);

        context.createProducer().setProperty("topicSubscription", "dweet").send(topic, subscription); // .setProperty("topicUser", tweet.getTopic()).send(topic, tweet);
    }
}