package rest.api;

import ejb.DeviceDao;
import ejb.SubscriptionDao;
import ejb.UserDao;
import entities.Subscription;
import entities.User;

import javax.ejb.EJB;
import javax.ejb.PostActivate;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
