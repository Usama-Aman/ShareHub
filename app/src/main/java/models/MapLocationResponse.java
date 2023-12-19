package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapLocationResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("data")
    @Expose
    private List<MapData> data = null;

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

    public List<MapData> getData() {
        return data;
    }

    public void setData(List<MapData> data) {
        this.data = data;
    }
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
        @SerializedName("fullImage")
        @Expose
        private String fullImage;
        @SerializedName("thumbImage")
        @Expose
        private String thumbImage;

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

    public class MapData {

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
        private String eventDetails;
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
        private Object eventPincode;
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
        @SerializedName("event_qr")
        @Expose
        private String eventQr;
        @SerializedName("isAttend")
        @Expose
        private Integer isAttend;
        @SerializedName("user_attend_event")
        @Expose
        private List<UserAttendEvent> userAttendEvent = null;
        @SerializedName("category_detail")
        @Expose
        private CategoryDetail categoryDetail;
        @SerializedName("fullImage")
        @Expose
        private String fullImage;
        @SerializedName("thumbImage")
        @Expose
        private String thumbImage;

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

        public String getEventDetails() {
            return eventDetails;
        }

        public void setEventDetails(String eventDetails) {
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

        public Object getEventPincode() {
            return eventPincode;
        }

        public void setEventPincode(Object eventPincode) {
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

        public String getEventQr() {
            return eventQr;
        }

        public void setEventQr(String eventQr) {
            this.eventQr = eventQr;
        }

        public Integer getIsAttend() {
            return isAttend;
        }

        public void setIsAttend(Integer isAttend) {
            this.isAttend = isAttend;
        }

        public List<UserAttendEvent> getUserAttendEvent() {
            return userAttendEvent;
        }

        public void setUserAttendEvent(List<UserAttendEvent> userAttendEvent) {
            this.userAttendEvent = userAttendEvent;
        }

        public CategoryDetail getCategoryDetail() {
            return categoryDetail;
        }

        public void setCategoryDetail(CategoryDetail categoryDetail) {
            this.categoryDetail = categoryDetail;
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
    public class CategoryDetail {

        @SerializedName("ecat_id")
        @Expose
        private Integer ecatId;
        @SerializedName("ecat_name")
        @Expose
        private String ecatName;
        @SerializedName("lang_id")
        @Expose
        private Integer langId;

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