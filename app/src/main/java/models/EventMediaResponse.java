package models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventMediaResponse {

    @SerializedName("data")
    @Expose
    private EventMediaResponseData data;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("isParticipant")
    @Expose
    private int isParticipant;
    @SerializedName("can_upload_media")
    @Expose
    private int can_upload_media;

    public int getCan_upload_media() {
        return can_upload_media;
    }

    public void setCan_upload_media(int can_upload_media) {
        this.can_upload_media = can_upload_media;
    }

    public int getIsParticipant() {
        return isParticipant;
    }

    public void setIsParticipant(int isParticipant) {
        this.isParticipant = isParticipant;
    }

    public EventMediaResponseData getData() {
        return data;
    }

    public void setData(EventMediaResponseData data) {
        this.data = data;
    }

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

}
