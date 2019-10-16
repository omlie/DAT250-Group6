package ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import entities.Device;
import entities.User;

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

    public List<Device> getDevices() {
        List<Device> reverseDeviceList = new ArrayList<>();
        reverseDeviceList.addAll(this.deviceDao.getAllDevices());
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

    public String addOwnedDevice(User user){
        if(device == null || user == null){
            return "index";
        }
        deviceDao.addOwner(user, device);
        return "mypage";
    }
}
