package rest.api;

import ejb.UserDao;
import entities.User;
import rest.models.RegisterUserRequest;
import org.mindrot.jbcrypt.BCrypt;

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
            return Response.status(Response.Status.NOT_FOUND).entity("Username already exists").build();
        }
        User user = new User();
        user.setUserName(request.username);
        user.setFirstName(request.firstname);
        user.setLastName(request.lastname);
        String password = BCrypt.hashpw(request.password, BCrypt.gensalt());
        user.setPassword(password);
        try {
            userDao.persist(user);
            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Could not create user").build();
        }
    }

    @POST
    @Transactional
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("username") String username,
                          @HeaderParam("password") String password) {
        try {
            if (userDao.login(userDao.getUser(username), username, password)) {
                return Response.ok(userDao.getUser(username)).build();
            }
        } catch(Exception ignored){
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Wrong username or password").build();
    }


    @GET
    @Path("{id}/devices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnedDevices(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);

        return Response.ok(userDao.getOwnedDevices(idInt)).build();
    }
}