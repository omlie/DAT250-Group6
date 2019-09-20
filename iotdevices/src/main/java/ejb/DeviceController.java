package ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.Path;

import entities.Device;

@Named(value = "deviceController")
@RequestScoped
public class DeviceController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // Injected DAO EJB:
    @EJB
    private DeviceDao deviceDao;

    private Device device;

    public String saveDevice() {
        this.deviceDao.persist(this.device);
        return "index";
    }

    public List<Device> getDevices() {
        List<Device> reverseDeviceList = new ArrayList<>();
        reverseDeviceList.addAll(this.deviceDao.getAllDevices());
        Collections.reverse(reverseDeviceList);
        return reverseDeviceList;
    }

    public Device getDeviceFromId(int deviceId) {
        return this.deviceDao.getDeviceFromId(deviceId);
    }

    public Device getDevice() {
        if (this.device == null) {
            device = new Device();
        }
        return device;

    }
}
