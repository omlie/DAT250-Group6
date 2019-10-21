package ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import entities.Device;
import entities.Label;
import helpers.Status;

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
            return "devices";
        }

        return "device";
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
}
