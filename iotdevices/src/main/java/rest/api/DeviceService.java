package rest.api;

import ejb.DeviceDao;
import ejb.UserDao;
import entities.Device;
import entities.Label;
import helpers.Status;

import javax.ejb.EJB;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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
    public Response getDevicesByLabel(@PathParam("label") String label){
        return Response.ok(deviceDao.filterDevicesByLabel(label)).build();
    }

    @GET
    @Path("search/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices(@PathParam("id") String label){
        int idInt = Integer.parseInt(label);
        return Response.ok(deviceDao.filterDevicesByLabelId(idInt)).build();
    }

    @POST
    @Path("create/device")
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


    private Status getStatus(int status){
        if(status == 0)
            return Status.ONLINE;
        return Status.OFFLINE;
    }

    private List<Label> getLabels(List<String> strings){
        List<Label> labels = new ArrayList<>();
        for(String s : strings){
            Label l = new Label();
            l.setLabelValue(s);
            labels.add(l);
        }
        return labels;
    }
}