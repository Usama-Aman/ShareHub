package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupListResponseData {

    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("group_dateadded")
    @Expose
    private String groupDateadded;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("group_modified_date")
    @Expose
    private String groupModifiedDate;
    @SerializedName("group_count_user")
    @Expose
    private Integer groupCountUser;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDateadded() {
        return groupDateadded;
    }

    public void setGroupDateadded(String groupDateadded) {
        this.groupDateadded = groupDateadded;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getGroupModifiedDate() {
        return groupModifiedDate;
    }

    public void setGroupModifiedDate(String groupModifiedDate) {
        this.groupModifiedDate = groupModifiedDate;
    }

    public Integer getGroupCountUser() {
        return groupCountUser;
    }

    public void setGroupCountUser(Integer groupCountUser) {
        this.groupCountUser = groupCountUser;
    }

}