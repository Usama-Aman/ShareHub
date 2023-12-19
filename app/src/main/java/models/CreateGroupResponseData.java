package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateGroupResponseData {

    @SerializedName("group")
    @Expose
    private CreateGroupResponseDataDetail group;
    @SerializedName("users")
    @Expose
    private List<User> users = null;

    public CreateGroupResponseDataDetail getGroup() {
        return group;
    }

    public void setGroup(CreateGroupResponseDataDetail group) {
        this.group = group;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}