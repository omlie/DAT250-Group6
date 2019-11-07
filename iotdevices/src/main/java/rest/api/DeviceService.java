package rest.api;

import ejb.DeviceDao;
import ejb.UserDao;
import entities.Device;
import entities.Feedback;
import entities.Label;
import entities.User;
import helpers.Status;

import javax.ejb.EJB;
import javax.ejb.PostActivate;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/devices")
public class DeviceService extends Application {

    @EJB
    private DeviceDao deviceDao;

    @EJB
    UserDao userDao;

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

    @GET
    @Path("search/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevicesByLabel(@PathParam("label") String label) {
        return Response.ok(deviceDao.filterDevicesByLabel(label)).build();
    }

    @GET
    @Path("search/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices(@PathParam("id") String label) {
        int idInt = Integer.parseInt(label);
        return Response.ok(deviceDao.filterDevicesByLabelId(idInt)).build();
    }

    @POST
    @Path("give/feedback/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitFeedback(@HeaderParam("feedback") String feedback,
                                   @HeaderParam("userId") int userid,
                                   @PathParam("id") int deviceid) {
        try {
            Feedback f = new Feedback();
            User user = userDao.getUser(userid);
            f.setAuthor(user);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            String formattedDate = dateFormat.format(date);
            f.setPublishedDate(formattedDate);
            f.setDevice(deviceDao.getDeviceById(deviceid));
            f.setFeedbackContent(feedback);
            deviceDao.addFeedback(f);
            return Response.ok(f).build();
        } catch (NotFoundException e) {
            return Response.status(404, "No such user" + userid).build();
        } catch (Exception e) {
            return Response.status(404, "Unknown error").build();
        }
    }

    @POST
    @Path("create")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDevice(@HeaderParam("devicename") String devicename,
                              @HeaderParam("apiurl") String apiurl,
                              @HeaderParam("status") int status,
                              @HeaderParam("labels") List<String> labels,
                              @HeaderParam("ownerId") int ownerId){
        Device device = new Device();
        device.setOwner(userDao.getUser(ownerId));
        device.setStatus(getStatus(status));
        device.setLabels(getLabels(labels));
        device.setDeviceName(devicename);
        device.setApiUrl(apiurl);
        deviceDao.createDevice(device);
        return Response.ok(device).build();
    }

    @POST
    @Path("delete/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDevice(@HeaderParam("username") String username,
                                 @PathParam("id") int id){
        try {
            userDao.deleteOwned(username, id);
            return Response.ok().build();
        } catch (NotFoundException e){
            return Response.status(404, "Could not delete device " + id).build();
        }
    }

    @POST
    @Path("unsubscribe/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response unsubscribe(@PathParam("id") int deviceid,
                                @HeaderParam("username") String username){
        try {
            userDao.unsubscribe(username, deviceid);
            return Response.ok().build();
        } catch(Exception e) {
            return Response.status(404, "Could not unsubscribe from " + deviceid).build();
        }
    }


    @POST
    @Path("edit/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response edit(@HeaderParam("devicename") String devicename,
                              @HeaderParam("apiurl") String apiurl,
                              @HeaderParam("status") int status,
                              @HeaderParam("labels") List<String> labels,
                              @PathParam("id") int id){
        try {
            Device device = deviceDao.getDeviceById(id);
            device.setStatus(getStatus(status));
            device.setLabels(getLabels(labels));
            device.setDeviceName(devicename);
            device.setApiUrl(apiurl);
            deviceDao.saveEditedDevice(device);
            return Response.ok(device).build();
        } catch(NotFoundException e){
            return Response.status(404, "Device " + id + " not found.").build();
        }
    }

    private Status getStatus(int status){
        if(status == 0)
            return Status.ONLINE;
        return Status.OFFLINE;
    }

    private List<Label> getLabels(List<String> strings){
        List<Label> labels = new ArrayList<>();
        for(String s : strings){
            Label l = deviceDao.addNewLabel(s);
            labels.add(l);
        }
        return labels;
    }
}