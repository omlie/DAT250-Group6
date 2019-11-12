package rest.api;

import ejb.DeviceDao;
import ejb.SubscriptionDao;
import ejb.UserDao;
import entities.Device;
import entities.Subscription;
import rest.models.DeviceModificationRequest;
import rest.models.SubscriptionStatus;

import javax.ejb.EJB;
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

    @EJB
    private DeviceDao deviceDao;

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
    @Path("pending/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pendingSubscribers(@PathParam("userid") int userid) {
        try {
            List<Subscription> subs = subDao.getPendingToOwned(userid);
            return Response.ok(subs).build();
        } catch (Exception e) {
            return Response.status(404).entity( "Unknown error").build();
        }
    }

    @GET
    @Path("mypending/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response myPendingSubscribers(@PathParam("userid") int userid) {
        try {
            List<Device> subs = subDao.getPendingDevices(userid);
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
            if (deviceDao.getDeviceById(deviceid).getOwner().getId() == userid)
                status = "Owner";

            Subscription s = subDao.getSubscription(deviceid, userid);
            if(s != null)
                if(s.isApprovedSubscription())
                    status = "Approved";
                else if(s.isDeniedSubscription())
                    status = "Denied";
                else
                    status = "Pending";

        } catch (Exception ignored){
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


    @POST
    @Path("unsubscribe/{deviceid}/{userid}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response unsubscribe(@PathParam("userid") int userid, @PathParam("deviceid") int deviceid) {
        try {
            userDao.unsubscribe(userid, deviceid);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(404).entity("Could not unsubscribe from " + deviceid).build();
        }
    }

}
