package models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlockedPeopleResponseData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("blocked_by")
    @Expose
    private Integer blockedBy;
    @SerializedName("blocked_datetime")
    @Expose
    private String blockedDatetime;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("user_fname")
    @Expose
    private Object userFname;
    @SerializedName("user_lname")
    @Expose
    private Object userLname;
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
    private Object userEventCategory;
    @SerializedName("user_intrest_area")
    @Expose
    private Object userIntrestArea;
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
    private Object apiToken;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(Integer blockedBy) {
        this.blockedBy = blockedBy;
    }

    public String getBlockedDatetime() {
        return blockedDatetime;
    }

    public void setBlockedDatetime(String blockedDatetime) {
        this.blockedDatetime = blockedDatetime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getUserFname() {
        return userFname;
    }

    public void setUserFname(Object userFname) {
        this.userFname = userFname;
    }

    public Object getUserLname() {
        return userLname;
    }

    public void setUserLname(Object userLname) {
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

    public Object getUserEventCategory() {
        return userEventCategory;
    }

    public void setUserEventCategory(Object userEventCategory) {
        this.userEventCategory = userEventCategory;
    }

    public Object getUserIntrestArea() {
        return userIntrestArea;
    }

    public void setUserIntrestArea(Object userIntrestArea) {
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

    public Object getApiToken() {
        return apiToken;
    }

    public void setApiToken(Object apiToken) {
        this.apiToken = apiToken;
    }
}