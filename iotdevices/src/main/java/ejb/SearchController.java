package ejb;

import entities.Device;

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
    DeviceDao dao;

    private List<Device> searchResult;
    private String searchWord;


    public void search(String searchWord){
        try {
            if(searchWord == null || searchWord.equals("")) {
                searchResult = dao.getOnlineDevices(dao.getAllDevices());
                return;
            }
            searchResult = dao.filterDevicesByLabel(searchWord);
        } catch (Exception e) {
            // redirect
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
            searchResult = dao.getOnlineDevices(dao.getAllDevices());
        return searchResult;
    }

    public void setSearchResult(List<Device> searchResult) {
        this.searchResult = searchResult;
    }
}
