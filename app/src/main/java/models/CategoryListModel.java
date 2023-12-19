package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryListModel {

    @SerializedName("categories")
    @Expose
    private List<CategoryModel> categories = null;
    @SerializedName("groups")
    @Expose
    private List<GroupsModel> groups = null;
    @SerializedName("users")
    @Expose
    private List<UserDetail> users = null;

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryModel> categories) {
        this.categories = categories;
    }

    public List<GroupsModel> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupsModel> groups) {
        this.groups = groups;
    }

    public List<UserDetail> getUsers() {
        return users;
    }

    public void setUsers(List<UserDetail> users) {
        this.users = users;
    }

}