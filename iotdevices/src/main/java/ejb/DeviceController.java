package ejb;

import entities.Device;
import helpers.Constants;
import helpers.Status;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entities.Label;

@Named(value = "deviceController")
@SessionScoped
public class DeviceController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // Injected DAO EJB:
    @EJB
    private DeviceDao deviceDao;

    private Device device;

    private Label newLabel;



    public List<Device> getDevices() {
        List<Device> reverseDeviceList = new ArrayList<>(this.deviceDao.getAllDevices());
        Collections.reverse(reverseDeviceList);
        return reverseDeviceList;
    }

    public String saveDevice() {
        this.deviceDao.persist(this.device);
        return "index";
    }

    public Device getDevice() {
        if (this.device == null) {
            device = new Device();
        }
        return device;
    }

    public String viewDevice(int deviceId) {
        try {
            this.device = this.deviceDao.getDeviceById(deviceId);
        } catch (Exception e) {
            return Constants.DEVICES;
        }

        return Constants.DEVICE;
    }

    public Device getDevice(int deviceid){
        this.device = deviceDao.getDeviceById(deviceid);
        return this.device;
    }

    public String editDevice() {
        if(device == null) {
            return "devices";
        }
        return "editdevice";
    }

    public void deleteLabel(Device d, Label l) {
        d.getLabels().remove(l);
        deviceDao.saveEditedDevice(d);
    }

    public List<Device> getOnlineDevices() {
        List<Device> devices = getDevices();
        for (Device d : devices)
            if (d.getStatus() == Status.OFFLINE)
                devices.remove(d);
        return devices;
    }

    public String mapStatusToCharacter(String status) {
        if (status.equals("ONLINE") || status.equals("AVAILABLE"))
            return "●";

        return "✕";
    }

    public Label getNewLabel() {
        return newLabel;
    }

    public void setNewLabel(Label newLabel) {
        this.newLabel = newLabel;
    }
}
