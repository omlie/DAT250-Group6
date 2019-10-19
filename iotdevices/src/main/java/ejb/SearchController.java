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
    private String searchWord;
    private List<Device> searchResult;

    @EJB
    DeviceDao deviceDao;

    public void search(String searchWord){
        try {
            if(searchWord == null) {
                searchResult = new ArrayList<>();
                return;
            }
            searchResult = deviceDao.filterDevicesByLabel(searchWord);
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
        return searchResult;
    }

    public void setSearchResult(List<Device> searchResult) {
        this.searchResult = searchResult;
    }

}
