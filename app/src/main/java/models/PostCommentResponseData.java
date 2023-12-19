package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by M.Mubashir on 5/23/2018.
 */

public class PostCommentResponseData {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public class Data {

        @SerializedName("event_id")
        @Expose
        private String eventId;
        @SerializedName("ecomment_text")
        @Expose
        private String ecommentText;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("ecomment_id")
        @Expose
        private Integer ecommentId;

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getEcommentText() {
            return ecommentText;
        }

        public void setEcommentText(String ecommentText) {
            this.ecommentText = ecommentText;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getEcommentId() {
            return ecommentId;
        }

        public void setEcommentId(Integer ecommentId) {
            this.ecommentId = ecommentId;
        }

    }
}