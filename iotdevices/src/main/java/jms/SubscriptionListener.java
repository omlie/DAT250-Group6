package jms;

import com.google.gson.JsonObject;
import entities.Subscription;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.IOException;

@MessageDriven(mappedName = "jms/dat250/Topic", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "topicSubscription = 'dweet'") })
public class SubscriptionListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            Subscription subscription = message.getBody(Subscription.class);
            JsonObject json = new JsonObject();

            json.addProperty("DeviceId", subscription.getDevice().getId());
            json.addProperty("DeviceName", subscription.getDevice().getDeviceName());
            json.addProperty("SubscriberUsername", subscription.getUser().getUserName());

            SubscriptionConnection subscriptionConnection = new SubscriptionConnection();
            subscriptionConnection.publish(json);

        } catch (JMSException | IOException e) {
            e.printStackTrace();
        }
    }
}
