package ejb;

import entities.Device;
import entities.Label;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named(value = "searchController")
@RequestScoped
public class SearchController implements Serializable{

    @EJB
    DeviceDao deviceDao;

    private List<Device> searchResult;
    private String searchWord;


    public void search(String searchWord){
        try {
            if(searchWord == null || searchWord.equals("")) {
                searchResult = deviceDao.getOnlineDevices(deviceDao.getAllDevices());
                return;
            }
            searchResult = deviceDao.filterDevicesByLabel(searchWord);
        } catch (Exception ignored) {
        }
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public List<Device> getSearchResult() {
        if(searchResult == null)
            searchResult = deviceDao.getOnlineDevices(deviceDao.getAllDevices());
        return searchResult;
    }

    public void setSearchResult(List<Device> searchResult) {
        this.searchResult = searchResult;
    }

}
