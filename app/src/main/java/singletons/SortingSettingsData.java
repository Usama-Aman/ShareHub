package singletons;

/**
 * Created by aamir on 5/31/2018.
 */

public class SortingSettingsData {

    private String type;
    private String sort_type;
    private String sort_field;
    private String distance_filter;
    private String date_filter;
    private String creator_filter;
    private String time_filter;
    private String location_filter;
    private String search_keyword;
    private int categoryID;
    public SortingSettingsData(String type, String sort_type, String sort_field, String distance_filter, String date_filter, String creator_filter, String time_filter, String location_filter, String search_keyword) {
        this.type = type;
        this.sort_type = sort_type;
        this.sort_field = sort_field;
        this.distance_filter = distance_filter;
        this.date_filter = date_filter;
        this.creator_filter = creator_filter;
        this.time_filter = time_filter;
        this.location_filter = location_filter;
        this.search_keyword = search_keyword;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSort_type() {
        return sort_type;
    }

    public void setSort_type(String sort_type) {
        this.sort_type = sort_type;
    }

    public String getSort_field() {
        return sort_field;
    }

    public void setSort_field(String sort_field) {
        this.sort_field = sort_field;
    }

    public String getDistance_filter() {
        return distance_filter;
    }

    public void setDistance_filter(String distance_filter) {
        this.distance_filter = distance_filter;
    }

    public String getDate_filter() {
        return date_filter;
    }

    public void setDate_filter(String date_filter) {
        this.date_filter = date_filter;
    }

    public String getCreator_filter() {
        return creator_filter;
    }

    public void setCreator_filter(String creator_filter) {
        this.creator_filter = creator_filter;
    }

    public String getTime_filter() {
        return time_filter;
    }

    public void setTime_filter(String time_filter) {
        this.time_filter = time_filter;
    }

    public String getLocation_filter() {
        return location_filter;
    }

    public void setLocation_filter(String location_filter) {
        this.location_filter = location_filter;
    }

    public String getSearch_keyword() {
        return search_keyword;
    }

    public void setSearch_keyword(String search_keyword) {
        this.search_keyword = search_keyword;
    }
}
