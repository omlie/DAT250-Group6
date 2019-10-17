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
    private String searchWord;
    private List<Device> searchResult;

    @EJB
    DeviceDao dao;

    public void search(String searchWord){
        if(searchWord == null) {
            searchResult = new ArrayList<>();
            return;
        }
        searchResult = dao.filterDevicesByLabel(searchWord);
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
