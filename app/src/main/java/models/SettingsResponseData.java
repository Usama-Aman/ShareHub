package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by M.Mubashir on 6/4/2018.
 */

public class SettingsResponseData {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public class Data {

        @SerializedName("user")
        @Expose
        private User user;
        @SerializedName("categories")
        @Expose
        private List<Category> categories = null;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public void setCategories(List<Category> categories) {
            this.categories = categories;
        }

    }

    public class User {

        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("user_visibility")
        @Expose
        private Integer userVisibility;
        @SerializedName("user_show_events")
        @Expose
        private Integer userShowEvents;
        @SerializedName("user_notification_mute")
        @Expose
        private Integer userNotificationMute;
        @SerializedName("user_intrest_area")
        @Expose
        private String userIntrestArea;
        @SerializedName("user_categories")
        @Expose
        private List<userCategories> userCategories = null;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
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

        public String getUserIntrestArea() {
            return userIntrestArea;
        }

        public void setUserIntrestArea(String userIntrestArea) {
            this.userIntrestArea = userIntrestArea;
        }

        public List<userCategories> getUserCategories() {
            return userCategories;
        }

        public void setUserCategories(List<userCategories> userCategories) {
            this.userCategories = userCategories;
        }

    }
    public static class userCategories {

        @SerializedName("ecat_id")
        @Expose
        private Integer ecatId;
        @SerializedName("ecat_name")
        @Expose
        private String ecatName;
        @SerializedName("ecat_name_ar")
        @Expose
        private String ecat_name_ar;

        public String getEcat_name_ar() {
            return ecat_name_ar;
        }

        public void setEcat_name_ar(String ecat_name_ar) {
            this.ecat_name_ar = ecat_name_ar;
        }

        public Integer getEcatId() {
            return ecatId;
        }

        public void setEcatId(Integer ecatId) {
            this.ecatId = ecatId;
        }

        public String getEcatName() {
            return ecatName;
        }

        public void setEcatName(String ecatName) {
            this.ecatName = ecatName;
        }


    }
    public class Category {

        @SerializedName("ecat_id")
        @Expose
        private Integer ecatId;
        @SerializedName("ecat_name")
        @Expose
        private String ecatName;
        @SerializedName("lang_id")
        @Expose
        private Integer langId;
        @SerializedName("ecat_name_ar")
        @Expose
        private String ecat_name_ar;

        public String getEcat_name_ar() {
            return ecat_name_ar;
        }

        public void setEcat_name_ar(String ecat_name_ar) {
            this.ecat_name_ar = ecat_name_ar;
        }

        public Integer getEcatId() {
            return ecatId;
        }

        public void setEcatId(Integer ecatId) {
            this.ecatId = ecatId;
        }

        public String getEcatName() {
            return ecatName;
        }

        public void setEcatName(String ecatName) {
            this.ecatName = ecatName;
        }

        public Integer getLangId() {
            return langId;
        }

        public void setLangId(Integer langId) {
            this.langId = langId;
        }

    }
}