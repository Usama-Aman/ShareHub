package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventMediaResponseDataDetail {

    @SerializedName("emedia_id")
    @Expose
    private Integer emediaId;
    @SerializedName("emedia_type")
    @Expose
    private String emediaType;
    @SerializedName("emedia_file")
    @Expose
    private String emediaFile;
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    @SerializedName("fullImage")
    @Expose
    private String fullImage;
    @SerializedName("thumbImage")
    @Expose
    private String thumbImage;
    @SerializedName("is_liked")
    @Expose
    private Boolean is_liked;

    public Boolean getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(Boolean is_liked) {
        this.is_liked = is_liked;
    }

    Boolean isSelected = false;
    Boolean isShowAll = false;
    Boolean isDeleteEnable = false;

    public Integer getEmediaId() {
        return emediaId;
    }

    public void setEmediaId(Integer emediaId) {
        this.emediaId = emediaId;
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

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
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

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }


    public Boolean getShowAll() {
        return isShowAll;
    }

    public void setShowAll(Boolean showAll) {
        isShowAll = showAll;
    }

    public Boolean getDeleteEnable() {
        return isDeleteEnable;
    }

    public void setDeleteEnable(Boolean deleteEnable) {
        isDeleteEnable = deleteEnable;
    }
}