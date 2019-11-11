package rest.api;

import ejb.SubscriptionDao;
import ejb.UserDao;
import entities.Subscription;
import rest.models.SubscriptionStatus;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/subscription")
public class SubscriptionService {

    @EJB
    private SubscriptionDao subDao;

    @EJB
    private UserDao userDao;

    @POST
    @Transactional
    @Path("approve/{deviceid}/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response approveSubscription(@PathParam("deviceid") int deviceid, @PathParam("userid") int userid) {
        try {
            Subscription s = subDao.getSubscription(deviceid, userid);
            subDao.approveSubscription(s);
            return Response.ok(s).build();
        } catch (NotFoundException e) {
            return Response.status(404).entity("Could not find subscription").build();
        }
    }

    @POST
    @Transactional
    @Path("deny/{deviceid}/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response denySubscriber(@PathParam("deviceid") int deviceId, @PathParam("userid") int userid) {
        try {
            Subscription s = subDao.getSubscription(deviceId, userid);
            subDao.denySubscription(s);
            return Response.ok(s).build();
        } catch (NotFoundException e) {
            return Response.status(404).entity( "Could not find subscription").build();
        }
    }

    @GET
    @Path("pending/{deviceid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pendingSubscribers(@PathParam("deviceid") int deviceId) {
        try {
            List<Subscription> subs = subDao.getPending(deviceId);
            return Response.ok(subs).build();
        } catch (Exception e) {
            return Response.status(404).entity( "Unknown error").build();
        }
    }

    @GET
    @Path("pending/{userid}/{deviceid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscription(@PathParam("deviceid") int deviceid, @PathParam("userid") int userid){
        try {
            Subscription s = subDao.getSubscription(deviceid, userid);
            return Response.ok(s).build();
        } catch (NotFoundException e){
            return Response.status(404).entity("No subscription").build();
        }
    }

    @GET
    @Path("status/{deviceid}/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriptionStatus(@PathParam("deviceid") int deviceid, @PathParam("userid") int userid){
        SubscriptionStatus subStatus = new SubscriptionStatus();
        String status = "none";
        try {
            Subscription s = subDao.getSubscription(deviceid, userid);
            if(s.isApprovedSubscription())
                status = "approved";
            else if(s.isDeniedSubscription())
                status = "denied";
            else
                status = "pending";
        } catch (EJBException ignored){
        }
        subStatus.subscriptionStatus = status;
        return Response.ok(subStatus).build();
    }

    @GET
    @Path("{id}/subscribedDevices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscribedDevices(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        return Response.ok(userDao.getSubscribedDevices(idInt)).build();
    }

    @POST
    @Transactional
    @Path("{id}/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubscriber(@PathParam("userid") int userId, @PathParam("id") String deviceId) {
        int idInt = Integer.parseInt(deviceId);
        userDao.addSubscriber(idInt, userId);
        return Response.ok().build();
    }

}
