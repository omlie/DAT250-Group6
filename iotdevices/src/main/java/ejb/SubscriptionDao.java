package ejb;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Subscription;

@Stateless
public class SubscriptionDao {
    // Injected database connection:
    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;


    public void approveSubscription(Subscription subscription) {
        subscription.setApprovedSubscription(true);
        em.merge(subscription);
    }
}