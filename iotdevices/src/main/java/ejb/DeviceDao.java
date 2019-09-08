package ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import entities.Device;

@Stateless
public class DeviceDao {
    // Injected database connection:
    @PersistenceContext(unitName="IOTDevices")
    private EntityManager em;

    // Stores a new tweet:
    public void persist(Device device) {
        em.persist(device);
    }

    // Retrieves all the tweets:
    @SuppressWarnings("unchecked")
    public List<Device> getAllDevices() {

        Query query = em.createQuery("SELECT d FROM Device d");
        List<Device> devices = new ArrayList<Device>();
        devices = query.getResultList();
        return devices;
    }
}