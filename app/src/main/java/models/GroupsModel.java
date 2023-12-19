package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupsModel implements Parcelable {

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

    private boolean isSelected = false;


    protected GroupsModel(Parcel in) {
        if (in.readByte() == 0) {
            groupId = null;
        } else {
            groupId = in.readInt();
        }
        groupName = in.readString();
        groupDateadded = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        groupModifiedDate = in.readString();
        if (in.readByte() == 0) {
            groupCountUser = null;
        } else {
            groupCountUser = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (groupId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(groupId);
        }
        dest.writeString(groupName);
        dest.writeString(groupDateadded);
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(groupModifiedDate);
        if (groupCountUser == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(groupCountUser);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupsModel> CREATOR = new Creator<GroupsModel>() {
        @Override
        public GroupsModel createFromParcel(Parcel in) {
            return new GroupsModel(in);
        }

        @Override
        public GroupsModel[] newArray(int size) {
            return new GroupsModel[size];
        }
    };

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

