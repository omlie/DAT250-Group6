package rest.api;

import ejb.DeviceDao;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/devices")
public class DeviceService extends Application {

    @EJB
    private DeviceDao deviceDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices() {
        return Response.ok(deviceDao.getAllDevices()).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevice(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        return Response.ok(deviceDao.getDeviceById(idInt)).build();
    }

    @GET
    @Path("{id}/subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscribers(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        return Response.ok(deviceDao.getSubscribers(idInt)).build();
    }

    @GET
    @Path("{deviceId}/subscription/{subscriptionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscribers(@PathParam("deviceId") String deviceId, @PathParam("subscriptionId") String subscriptionId) {
        int subscriptionInt = Integer.parseInt(subscriptionId);
        int deviceInt = Integer.parseInt(deviceId);
        return Response.ok(deviceDao.getSubscriptionInfo(deviceInt, subscriptionInt)).build();
    }

    @GET
    @Path("{id}/feedback")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeedBack(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        return Response.ok(deviceDao.getFeedback(idInt)).build();
    }

    @POST
    @Transactional
    @Path("{id}")
    public Response addSubscriber(@HeaderParam("userId") int userId, @PathParam("id") String deviceId) {
        int idInt = Integer.parseInt(deviceId);
        deviceDao.addSubscriber(idInt, userId);
        return Response.ok().build();
    }

    @GET
    @Path("search/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevicesByLabel(@PathParam("label") String label){
        return Response.ok(deviceDao.filterDevicesByLabel(label)).build();
    }

    @GET
    @Path("search/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices(@PathParam("id") String label){
        int idInt = Integer.parseInt(label);
        return Response.ok(deviceDao.filterDevicesByLabelId(idInt)).build();
    }
}