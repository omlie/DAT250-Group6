package rest.api;

import entities.User;

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

    @PersistenceContext(unitName = "IOTDevices")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_ALL, User.class);
        List<User> users = query.getResultList();
        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        User user = em.find(User.class, idInt);
        if (user == null)
            throw new NotFoundException();
        return Response.ok(user).build();
    }

    @GET
    @Path("{id}/devices")
    public Response getOwnedDevices(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        User user = em.find(User.class, idInt);
        if (user == null)
            throw new NotFoundException();
        return Response.ok(user.getOwnedDevices()).build();
    }

    @GET
    @Path("{id}/subscribedDevices")
    public Response getSubscribedDevices(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        User user = em.find(User.class, idInt);
        if (user == null)
            throw new NotFoundException();
        return Response.ok(user).build();
    }
}