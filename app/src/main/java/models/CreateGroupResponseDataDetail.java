package models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateGroupResponseDataDetail {

    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("group_count_user")
    @Expose
    private Integer groupCountUser;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGroupCountUser() {
        return groupCountUser;
    }

    public void setGroupCountUser(Integer groupCountUser) {
        this.groupCountUser = groupCountUser;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

}
