package models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserMyEventResponse {

    @SerializedName("event_id")
    @Expose
    private Integer eventId;
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
    @SerializedName("event_coverphoto")
    @Expose
    private String eventCoverphoto;
    @SerializedName("event_location")
    @Expose
    private String eventLocation;
    @SerializedName("event_venue_lat")
    @Expose
    private String eventVenueLat;
    @SerializedName("event_venue_long")
    @Expose
    private String eventVenueLong;
    @SerializedName("event_details")
    @Expose
    private Object eventDetails;
    @SerializedName("ecat_id")
    @Expose
    private Integer ecatId;
    @SerializedName("event_iscomments_allowed")
    @Expose
    private Integer eventIscommentsAllowed;
    @SerializedName("event_isprivate")
    @Expose
    private Integer eventIsprivate;
    @SerializedName("event_isPincode_required")
    @Expose
    private Integer eventIsPincodeRequired;
    @SerializedName("event_pincode")
    @Expose
    private String eventPincode;
    @SerializedName("event_dateadded")
    @Expose
    private String eventDateadded;
    @SerializedName("event_count_participants")
    @Expose
    private Integer eventCountParticipants;
    @SerializedName("event_link")
    @Expose
    private Object eventLink;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("event_isApproval_required")
    @Expose
    private Integer eventIsApprovalRequired;
    @SerializedName("event_isApproved")
    @Expose
    private Integer eventIsApproved;
    @SerializedName("event_count_comments")
    @Expose
    private Integer eventCountComments;
    @SerializedName("fullImage")
    @Expose
    private String fullImage;
    @SerializedName("thumbImage")
    @Expose
    private String thumbImage;
    String eventType= new String();
    @SerializedName("isLive")
    @Expose
    private Integer isLive;
    @SerializedName("user_attend_event")
    @Expose
    private List<EventsResponse.UserAttendEvent> user_attend_event;
    @SerializedName("media_list")
    @Expose
    private ArrayList<MediaList> mediaList = null;

    public ArrayList<MediaList> getMediaList() {
        return mediaList;
    }

    public void setMediaList(ArrayList<MediaList> mediaList) {
        this.mediaList = mediaList;
    }

    public List<EventsResponse.UserAttendEvent> getUser_attend_event() {
        return user_attend_event;
    }

    public void setUser_attend_event(List<EventsResponse.UserAttendEvent> user_attend_event) {
        this.user_attend_event = user_attend_event;
    }

    public Integer getIsLive() {
        return isLive;
    }

    public void setIsLive(Integer isLive) {
        this.isLive = isLive;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
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

    public String getEventCoverphoto() {
        return eventCoverphoto;
    }

    public void setEventCoverphoto(String eventCoverphoto) {
        this.eventCoverphoto = eventCoverphoto;
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

    public Object getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(Object eventDetails) {
        this.eventDetails = eventDetails;
    }

    public Integer getEcatId() {
        return ecatId;
    }

    public void setEcatId(Integer ecatId) {
        this.ecatId = ecatId;
    }

    public Integer getEventIscommentsAllowed() {
        return eventIscommentsAllowed;
    }

    public void setEventIscommentsAllowed(Integer eventIscommentsAllowed) {
        this.eventIscommentsAllowed = eventIscommentsAllowed;
    }

    public Integer getEventIsprivate() {
        return eventIsprivate;
    }

    public void setEventIsprivate(Integer eventIsprivate) {
        this.eventIsprivate = eventIsprivate;
    }

    public Integer getEventIsPincodeRequired() {
        return eventIsPincodeRequired;
    }

    public void setEventIsPincodeRequired(Integer eventIsPincodeRequired) {
        this.eventIsPincodeRequired = eventIsPincodeRequired;
    }

    public String getEventPincode() {
        return eventPincode;
    }

    public void setEventPincode(String eventPincode) {
        this.eventPincode = eventPincode;
    }

    public String getEventDateadded() {
        return eventDateadded;
    }

    public void setEventDateadded(String eventDateadded) {
        this.eventDateadded = eventDateadded;
    }

    public Integer getEventCountParticipants() {
        return eventCountParticipants;
    }

    public void setEventCountParticipants(Integer eventCountParticipants) {
        this.eventCountParticipants = eventCountParticipants;
    }

    public Object getEventLink() {
        return eventLink;
    }

    public void setEventLink(Object eventLink) {
        this.eventLink = eventLink;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEventIsApprovalRequired() {
        return eventIsApprovalRequired;
    }

    public void setEventIsApprovalRequired(Integer eventIsApprovalRequired) {
        this.eventIsApprovalRequired = eventIsApprovalRequired;
    }

    public Integer getEventIsApproved() {
        return eventIsApproved;
    }

    public void setEventIsApproved(Integer eventIsApproved) {
        this.eventIsApproved = eventIsApproved;
    }

    public Integer getEventCountComments() {
        return eventCountComments;
    }

    public void setEventCountComments(Integer eventCountComments) {
        this.eventCountComments = eventCountComments;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}