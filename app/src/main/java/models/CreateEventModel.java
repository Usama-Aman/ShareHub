package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateEventModel {

    @SerializedName("event_isprivate")
    @Expose
    private Integer eventIsprivate;
    @SerializedName("event_coverphoto")
    @Expose
    private String eventCoverphoto;
    @SerializedName("event_title")
    @Expose
    private String eventTitle;
    @SerializedName("event_description")
    @Expose
    private String eventDescription;
    @SerializedName("event_start_date")
    @Expose
    private String eventStartDate;
    @SerializedName("event_start_time")
    @Expose
    private String eventStartTime;
    @SerializedName("event_end_date")
    @Expose
    private String eventEndDate;
    @SerializedName("event_end_time")
    @Expose
    private String eventEndTime;
    @SerializedName("event_location")
    @Expose
    private String eventLocation;
    @SerializedName("event_venue_lat")
    @Expose
    private String eventVenueLat;
    @SerializedName("event_venue_long")
    @Expose
    private String eventVenueLong;
    @SerializedName("ecat_id")
    @Expose
    private String ecatId;
    @SerializedName("event_iscomments_allowed")
    @Expose
    private Integer eventIscommentsAllowed;
    @SerializedName("event_isApproval_required")
    @Expose
    private Integer eventIsApprovalRequired;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    @SerializedName("fullImage")
    @Expose
    private String fullImage;
    @SerializedName("thumbImage")
    @Expose
    private String thumbImage;

    public Integer getEventIsprivate() {
        return eventIsprivate;
    }

    public void setEventIsprivate(Integer eventIsprivate) {
        this.eventIsprivate = eventIsprivate;
    }

    public String getEventCoverphoto() {
        return eventCoverphoto;
    }

    public void setEventCoverphoto(String eventCoverphoto) {
        this.eventCoverphoto = eventCoverphoto;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventVenueLat() {
        return eventVenueLat;
    }

    public void setEventVenueLat(String eventVenueLat) {
        this.eventVenueLat = eventVenueLat;
    }

    public String getEventVenueLong() {
        return eventVenueLong;
    }

    public void setEventVenueLong(String eventVenueLong) {
        this.eventVenueLong = eventVenueLong;
    }

    public String getEcatId() {
        return ecatId;
    }

    public void setEcatId(String ecatId) {
        this.ecatId = ecatId;
    }

    public Integer getEventIscommentsAllowed() {
        return eventIscommentsAllowed;
    }

    public void setEventIscommentsAllowed(Integer eventIscommentsAllowed) {
        this.eventIscommentsAllowed = eventIscommentsAllowed;
    }

    public Integer getEventIsApprovalRequired() {
        return eventIsApprovalRequired;
    }

    public void setEventIsApprovalRequired(Integer eventIsApprovalRequired) {
        this.eventIsApprovalRequired = eventIsApprovalRequired;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

}