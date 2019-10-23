package rest.api;

import ejb.UserDao;

import javax.ejb.EJB;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
public class UserService extends Application {

    @EJB
    private UserDao userDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        return Response.ok(userDao.getAllUsers()).build();
    }

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);

        return Response.ok(userDao.getUser(idInt)).build();
    }

    @GET
    @Path("{id}/devices")
    public Response getOwnedDevices(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);

        return Response.ok(userDao.getOwnedDevices(idInt)).build();
    }

    @GET
    @Path("{id}/subscribedDevices")
    public Response getSubscribedDevices(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        return Response.ok(userDao.getSubscribedDevices(idInt)).build();
    }

    @POST
    @Transactional
    @Path("{id}")
    public Response addSubscriber(@HeaderParam("userId") int userId, @PathParam("id") String deviceId) {
        int idInt = Integer.parseInt(deviceId);
        userDao.addSubscriber(idInt, userId);
        return Response.ok().build();
    }
}