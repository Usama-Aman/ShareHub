package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAttendEvent {

        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("user_fullname")
        @Expose
        private String userFullname;
        @SerializedName("user_photo")
        @Expose
        private String userPhoto;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getUserFullname() {
            return userFullname;
        }

        public void setUserFullname(String userFullname) {
            this.userFullname = userFullname;
        }

        public String getUserPhoto() {
            return userPhoto;
        }

        public void setUserPhoto(String userPhoto) {
            this.userPhoto = userPhoto;
        }

    }