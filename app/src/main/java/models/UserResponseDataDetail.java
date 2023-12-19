package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponseDataDetail {

    @SerializedName("gpeople_id")
    @Expose
    private Integer gpeopleId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("gpeople_isblocked")
    @Expose
    private Integer gpeopleIsblocked;
    @SerializedName("user_detail")
    @Expose
    private UserDetail userDetail;

    public Integer getGpeopleId() {
        return gpeopleId;
    }

    public void setGpeopleId(Integer gpeopleId) {
        this.gpeopleId = gpeopleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getGpeopleIsblocked() {
        return gpeopleIsblocked;
    }

    public void setGpeopleIsblocked(Integer gpeopleIsblocked) {
        this.gpeopleIsblocked = gpeopleIsblocked;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

}
