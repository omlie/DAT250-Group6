package rest.api;

import ejb.UserDao;
import entities.User;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);

        return Response.ok(userDao.getUser(idInt)).build();
    }

    @POST
    @Transactional
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@HeaderParam("username") String username,
                                 @HeaderParam("firstname") String firstname,
                                 @HeaderParam("lastname") String lastname,
                                 @HeaderParam("password") String password) {
        if (userDao.userExists(username)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User user = new User();
        user.setUserName(username);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setPassword(password);
        if (userDao.createUser(user))
            return Response.ok(user).build();
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Transactional
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("username") String username, @HeaderParam("password") String password) {
        if (userDao.login(username, password)) {
            return Response.ok(userDao.getUser(username)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @GET
    @Path("{id}/devices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnedDevices(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);

        return Response.ok(userDao.getOwnedDevices(idInt)).build();
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
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubscriber(@HeaderParam("userId") int userId, @PathParam("id") String deviceId) {
        int idInt = Integer.parseInt(deviceId);
        userDao.addSubscriber(idInt, userId);
        return Response.ok().build();
    }
}