package ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import entities.Device;
import entities.Label;

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

    private Label l1;
    private Label l2;
    private Label l3;

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

    public String addOwnedDevice(int userid){
        if(device == null){
            return "index";
        }
        if(l1 != null)
            device.addLabel(l1);
        if(l2 != null)
            device.addLabel(l2);
        if(l3 != null)
            device.addLabel(l3);

        deviceDao.addOwner(userid, device);
        return "mypage";
    }

    public Label getL1() {
        l1 = new Label();
        return l1;
    }

    public Label getL2() {
        l2 = new Label();
        return l2;
    }

    public Label getL3() {
        l3 = new Label();
        return l3;
    }
}
