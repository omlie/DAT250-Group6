package rest.api;

import entities.Device;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/devices")
public class DeviceService extends Application {

    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices() {
        TypedQuery<Device> query = em.createNamedQuery(Device.FIND_ALL, Device.class);
        List<Device> devices = query.getResultList();
        return Response.ok(devices).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevice(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        Device device = em.find(Device.class, idInt);
        if (device == null)
            throw new NotFoundException();
        return Response.ok(device).build();
    }

    @GET
    @Path("{id}/subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscribers(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        Device device = em.find(Device.class, idInt);
        if (device == null)
            throw new NotFoundException();
        return Response.ok(device.getSubscribers()).build();
    }

    @Transactional
    @POST
    @Path("{id}")
    public Response getSubscribers(@HeaderParam("userId") int userId, @PathParam("id") String deviceId) {
        int idInt = Integer.parseInt(deviceId);
        Device device = em.find(Device.class, idInt);
        User user = em.find(User.class, userId);
        if (device == null || user == null)
            throw new NotFoundException();
        device.addSubscriber(user);
        user.addSubscribedDevice(device);
        em.persist(device);
        em.persist(user);
        return Response.ok().build();
    }
}