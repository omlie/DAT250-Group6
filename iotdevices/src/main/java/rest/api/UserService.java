package rest.api;

import ejb.UserDao;
import entities.User;
import rest.models.RegisterUserRequest;

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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterUserRequest request) {
        if (userDao.userExists(request.username)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User user = new User();
        user.setUserName(request.username);
        user.setFirstName(request.firstname);
        user.setLastName(request.lastname);
        user.setPassword(request.password);
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
}