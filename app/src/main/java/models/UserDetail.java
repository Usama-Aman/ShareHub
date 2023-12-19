package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetail implements Parcelable {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_fname")
    @Expose
    private String userFname;
    @SerializedName("user_lname")
    @Expose
    private String userLname;
    @SerializedName("user_fullname")
    @Expose
    private String userFullname;
    @SerializedName("user_mobile_number")
    @Expose
    private String userMobileNumber;
    @SerializedName("user_sms_token")
    @Expose
    private Integer userSmsToken;
    @SerializedName("user_is_active")
    @Expose
    private Integer userIsActive;
    @SerializedName("user_visibility")
    @Expose
    private Integer userVisibility;
    @SerializedName("user_show_events")
    @Expose
    private Integer userShowEvents;
    @SerializedName("user_notification_mute")
    @Expose
    private Integer userNotificationMute;
    @SerializedName("user_event_category")
    @Expose
    private String userEventCategory;
    @SerializedName("user_intrest_area")
    @Expose
    private String userIntrestArea;
    @SerializedName("user_photo")
    @Expose
    private String userPhoto;
    @SerializedName("user_created_events_count")
    @Expose
    private Integer userCreatedEventsCount;
    @SerializedName("user_participated_events_count")
    @Expose
    private Integer userParticipatedEventsCount;
    @SerializedName("user_groups_count")
    @Expose
    private Integer userGroupsCount;
    @SerializedName("user_followers_count")
    @Expose
    private Integer userFollowersCount;
    @SerializedName("user_isVerified")
    @Expose
    private Integer userIsVerified;
    @SerializedName("user_preferred_language")
    @Expose
    private Integer userPreferredLanguage;
    @SerializedName("user_dateadded")
    @Expose
    private String userDateadded;
    @SerializedName("api_token")
    @Expose
    private String apiToken;
    @SerializedName("event_life_time")
    @Expose
    private Integer eventLifeTime;
    @SerializedName("remember_token")
    @Expose
    private String rememberToken;
    @SerializedName("user_address")
    @Expose
    private String userAddress;
    @SerializedName("fullImage")
    @Expose
    private String fullImage;
    @SerializedName("thumbImage")
    @Expose
    private String thumbImage;
    private boolean isSelected;


    protected UserDetail(Parcel in) {
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        email = in.readString();
        userFname = in.readString();
        userLname = in.readString();
        userFullname = in.readString();
        userMobileNumber = in.readString();
        if (in.readByte() == 0) {
            userSmsToken = null;
        } else {
            userSmsToken = in.readInt();
        }
        if (in.readByte() == 0) {
            userIsActive = null;
        } else {
            userIsActive = in.readInt();
        }
        if (in.readByte() == 0) {
            userVisibility = null;
        } else {
            userVisibility = in.readInt();
        }
        if (in.readByte() == 0) {
            userShowEvents = null;
        } else {
            userShowEvents = in.readInt();
        }
        if (in.readByte() == 0) {
            userNotificationMute = null;
        } else {
            userNotificationMute = in.readInt();
        }
        userEventCategory = in.readString();
        userIntrestArea = in.readString();
        userPhoto = in.readString();
        if (in.readByte() == 0) {
            userCreatedEventsCount = null;
        } else {
            userCreatedEventsCount = in.readInt();
        }
        if (in.readByte() == 0) {
            userParticipatedEventsCount = null;
        } else {
            userParticipatedEventsCount = in.readInt();
        }
        if (in.readByte() == 0) {
            userGroupsCount = null;
        } else {
            userGroupsCount = in.readInt();
        }
        if (in.readByte() == 0) {
            userFollowersCount = null;
        } else {
            userFollowersCount = in.readInt();
        }
        if (in.readByte() == 0) {
            userIsVerified = null;
        } else {
            userIsVerified = in.readInt();
        }
        if (in.readByte() == 0) {
            userPreferredLanguage = null;
        } else {
            userPreferredLanguage = in.readInt();
        }
        userDateadded = in.readString();
        apiToken = in.readString();
        if (in.readByte() == 0) {
            eventLifeTime = null;
        } else {
            eventLifeTime = in.readInt();
        }
        rememberToken = in.readString();
        userAddress = in.readString();
        fullImage = in.readString();
        thumbImage = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(email);
        dest.writeString(userFname);
        dest.writeString(userLname);
        dest.writeString(userFullname);
        dest.writeString(userMobileNumber);
        if (userSmsToken == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userSmsToken);
        }
        if (userIsActive == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userIsActive);
        }
        if (userVisibility == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userVisibility);
        }
        if (userShowEvents == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userShowEvents);
        }
        if (userNotificationMute == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userNotificationMute);
        }
        dest.writeString(userEventCategory);
        dest.writeString(userIntrestArea);
        dest.writeString(userPhoto);
        if (userCreatedEventsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userCreatedEventsCount);
        }
        if (userParticipatedEventsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userParticipatedEventsCount);
        }
        if (userGroupsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userGroupsCount);
        }
        if (userFollowersCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userFollowersCount);
        }
        if (userIsVerified == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userIsVerified);
        }
        if (userPreferredLanguage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userPreferredLanguage);
        }
        dest.writeString(userDateadded);
        dest.writeString(apiToken);
        if (eventLifeTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventLifeTime);
        }
        dest.writeString(rememberToken);
        dest.writeString(userAddress);
        dest.writeString(fullImage);
        dest.writeString(thumbImage);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserDetail> CREATOR = new Creator<UserDetail>() {
        @Override
        public UserDetail createFromParcel(Parcel in) {
            return new UserDetail(in);
        }

        @Override
        public UserDetail[] newArray(int size) {
            return new UserDetail[size];
        }
    };

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserFname() {
        return userFname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public String getUserLname() {
        return userLname;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    public Integer getUserSmsToken() {
        return userSmsToken;
    }

    public void setUserSmsToken(Integer userSmsToken) {
        this.userSmsToken = userSmsToken;
    }

    public Integer getUserIsActive() {
        return userIsActive;
    }

    public void setUserIsActive(Integer userIsActive) {
        this.userIsActive = userIsActive;
    }

    public Integer getUserVisibility() {
        return userVisibility;
    }

    public void setUserVisibility(Integer userVisibility) {
        this.userVisibility = userVisibility;
    }

    public Integer getUserShowEvents() {
        return userShowEvents;
    }

    public void setUserShowEvents(Integer userShowEvents) {
        this.userShowEvents = userShowEvents;
    }

    public Integer getUserNotificationMute() {
        return userNotificationMute;
    }

    public void setUserNotificationMute(Integer userNotificationMute) {
        this.userNotificationMute = userNotificationMute;
    }

    public String getUserEventCategory() {
        return userEventCategory;
    }

    public void setUserEventCategory(String userEventCategory) {
        this.userEventCategory = userEventCategory;
    }

    public String getUserIntrestArea() {
        return userIntrestArea;
    }

    public void setUserIntrestArea(String userIntrestArea) {
        this.userIntrestArea = userIntrestArea;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Integer getUserCreatedEventsCount() {
        return userCreatedEventsCount;
    }

    public void setUserCreatedEventsCount(Integer userCreatedEventsCount) {
        this.userCreatedEventsCount = userCreatedEventsCount;
    }

    public Integer getUserParticipatedEventsCount() {
        return userParticipatedEventsCount;
    }

    public void setUserParticipatedEventsCount(Integer userParticipatedEventsCount) {
        this.userParticipatedEventsCount = userParticipatedEventsCount;
    }

    public Integer getUserGroupsCount() {
        return userGroupsCount;
    }

    public void setUserGroupsCount(Integer userGroupsCount) {
        this.userGroupsCount = userGroupsCount;
    }

    public Integer getUserFollowersCount() {
        return userFollowersCount;
    }

    public void setUserFollowersCount(Integer userFollowersCount) {
        this.userFollowersCount = userFollowersCount;
    }

    public Integer getUserIsVerified() {
        return userIsVerified;
    }

    public void setUserIsVerified(Integer userIsVerified) {
        this.userIsVerified = userIsVerified;
    }

    public Integer getUserPreferredLanguage() {
        return userPreferredLanguage;
    }

    public void setUserPreferredLanguage(Integer userPreferredLanguage) {
        this.userPreferredLanguage = userPreferredLanguage;
    }

    public String getUserDateadded() {
        return userDateadded;
    }

    public void setUserDateadded(String userDateadded) {
        this.userDateadded = userDateadded;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public Integer getEventLifeTime() {
        return eventLifeTime;
    }

    public void setEventLifeTime(Integer eventLifeTime) {
        this.eventLifeTime = eventLifeTime;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getFullImage() {
        return fullImage;
    }

    public void setFullImage(String fullImage) {
        this.fullImage = fullImage;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}

