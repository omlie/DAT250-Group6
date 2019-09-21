package rest.api;

import entities.Device;

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
}