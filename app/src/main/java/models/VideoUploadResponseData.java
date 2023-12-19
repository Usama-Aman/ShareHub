package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoUploadResponseData {
    @SerializedName("event_id")
    @Expose
    private String eventId;
    @SerializedName("emedia_type")
    @Expose
    private String emediaType;
    @SerializedName("emedia_file")
    @Expose
    private String emediaFile;
    @SerializedName("emedia_id")
    @Expose
    private Integer emediaId;
    @SerializedName("fullImage")
    @Expose
    private String fullImage;
    @SerializedName("thumbImage")
    @Expose
    private String thumbImage;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEmediaType() {
        return emediaType;
    }

    public void setEmediaType(String emediaType) {
        this.emediaType = emediaType;
    }

    public String getEmediaFile() {
        return emediaFile;
    }

    public void setEmediaFile(String emediaFile) {
        this.emediaFile = emediaFile;
    }

    public Integer getEmediaId() {
        return emediaId;
    }

    public void setEmediaId(Integer emediaId) {
        this.emediaId = emediaId;
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

}
