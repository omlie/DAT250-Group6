package rest.models;

public class SubscriptionStatus {
    public String subscriptionStatus;

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public SubscriptionStatus(String status){
        this.subscriptionStatus = status;
    }

    public SubscriptionStatus(){

    }
}
