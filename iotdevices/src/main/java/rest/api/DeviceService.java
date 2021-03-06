package rest.api;

import ejb.DeviceDao;
import ejb.UserDao;
import entities.Device;
import entities.Feedback;
import entities.Label;
import entities.User;
import helpers.Status;
import rest.models.DeviceRequest;
import rest.models.DeviceModificationRequest;
import rest.models.FeedbackAddRequest;

import javax.ejb.EJB;
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
    @Path("{id}/approvedSubscribers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApprovedSubscribers(@PathParam("id") String id) {
        int idInt = Integer.parseInt(id);
        return Response.ok(deviceDao.getApprovedSubscribers(idInt)).build();
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
    @Path("feedback")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitFeedback(FeedbackAddRequest request) {
        try {
            Feedback f = new Feedback();
            User user = userDao.getUser(request.userid);
            f.setAuthor(user);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            String formattedDate = dateFormat.format(date);
            f.setPublishedDate(formattedDate);
            f.setDevice(deviceDao.getDeviceById(request.deviceid));
            f.setFeedbackContent(request.feedback);
            deviceDao.addFeedback(f);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(404).entity("No such user" + request.userid).build();
        } catch (Exception e) {
            return Response.status(404).entity("Unknown error").build();
        }
    }

    @POST
    @Path("create")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDevice(DeviceRequest request) {
        Device device = new Device();
        device.setOwner(userDao.getUser(request.ownerId));
        addFieldsToDevice(device, request.status, request.labels, request.devicename, request.apiurl);
        device.setDeviceImg(request.deviceimg);
        deviceDao.createDevice(device);
        return Response.ok(device).build();
    }

    @POST
    @Path("delete")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDevice(@HeaderParam("userId") int userid, @HeaderParam("deviceId") int deviceid) {
        try {
            userDao.deleteOwned(userid, deviceid);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(404).entity("Could not delete device " + deviceid).build();
        }
    }


    @POST
    @Path("edit/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response edit(DeviceRequest request, @PathParam("id") int deviceId) {
        try {
            Device device = deviceDao.getDeviceById(deviceId);
            addFieldsToDevice(device, request.status, request.labels, request.devicename, request.apiurl);
            deviceDao.saveEditedDevice(device);
            return Response.ok(device).build();
        } catch (NotFoundException e) {
            return Response.status(404).entity("Device " + deviceId + " not found.").build();
        }
    }

    private void addFieldsToDevice(Device device, Integer status, List<String> labels, String devicename, String apiurl ) {
        device.setStatus(getStatus(status));
        device.setLabels(getLabels(labels));
        device.setDeviceName(devicename);
        device.setApiUrl(apiurl);
    }

    private Status getStatus(int status) {
        if (status == 0)
            return Status.ONLINE;
        return Status.OFFLINE;
    }

    private List<Label> getLabels(List<String> strings) {
        List<Label> labels = new ArrayList<>();
        for (String s : strings) {
            Label l = deviceDao.addNewLabel(s);
            labels.add(l);
        }
        return labels;
    }
}