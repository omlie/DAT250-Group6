package rest.api;

import ejb.UserDao;
import entities.User;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
public class UserService extends Application {

    @EJB
    private UserDao userDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        return Response.ok(userDao.getUsers()).build();
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
}