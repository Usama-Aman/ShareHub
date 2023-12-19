package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M.Mubashir on 5/17/2018.
 */

public class EventsResponse {


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
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    public static class EventData {

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
        private String eventPincode;
        @SerializedName("event_dateadded")
        @Expose
        private String eventDateadded;
        @SerializedName("event_count_participants")
        @Expose
        private Integer eventCountParticipants;
        @SerializedName("event_link")
        @Expose
        private String eventLink;
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
        @SerializedName("isAttend")
        @Expose
        private Integer isAttend;
        @SerializedName("user_attend_event")
        @Expose
        private List<UserAttendEvent> user_attend_event;
        @SerializedName("media_list")
        @Expose
        private ArrayList<MediaList> mediaList;

        public ArrayList<MediaList> getMediaList() {
            return mediaList;
        }

        public void setMediaList(ArrayList<MediaList> mediaList) {
            this.mediaList = mediaList;
        }

        public Integer getIsAttend() {
            return isAttend;
        }

        public void setIsAttend(Integer isAttend) {
            this.isAttend = isAttend;
        }

        public List<UserAttendEvent> getUserAttendEvent() {
            return user_attend_event;
        }

        public void setUserAttendEvent(List<UserAttendEvent> userAttendEvent) {
            this.user_attend_event = userAttendEvent;
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

        public String getEventLink() {
            return eventLink;
        }

        public void setEventLink(String eventLink) {
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
    }
    public class Data {

        @SerializedName("current_page")
        @Expose
        private Integer currentPage;
        @SerializedName("data")
        @Expose
        private List<EventData> data = null;
        @SerializedName("first_page_url")
        @Expose
        private String firstPageUrl;
        @SerializedName("from")
        @Expose
        private Integer from;
        @SerializedName("last_page")
        @Expose
        private Integer lastPage;
        @SerializedName("last_page_url")
        @Expose
        private String lastPageUrl;
        @SerializedName("next_page_url")
        @Expose
        private String nextPageUrl;
        @SerializedName("path")
        @Expose
        private String path;
        @SerializedName("per_page")
        @Expose
        private Integer perPage;
        @SerializedName("prev_page_url")
        @Expose
        private String prevPageUrl;
        @SerializedName("to")
        @Expose
        private Integer to;
        @SerializedName("total")
        @Expose
        private Integer total;

        public Integer getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public List<EventData> getData() {
            return data;
        }

        public void setData(List<EventData> data) {
            this.data = data;
        }

        public String getFirstPageUrl() {
            return firstPageUrl;
        }

        public void setFirstPageUrl(String firstPageUrl) {
            this.firstPageUrl = firstPageUrl;
        }

        public Integer getFrom() {
            return from;
        }

        public void setFrom(Integer from) {
            this.from = from;
        }

        public Integer getLastPage() {
            return lastPage;
        }

        public void setLastPage(Integer lastPage) {
            this.lastPage = lastPage;
        }

        public String getLastPageUrl() {
            return lastPageUrl;
        }

        public void setLastPageUrl(String lastPageUrl) {
            this.lastPageUrl = lastPageUrl;
        }

        public String getNextPageUrl() {
            return nextPageUrl;
        }

        public void setNextPageUrl(String nextPageUrl) {
            this.nextPageUrl = nextPageUrl;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Integer getPerPage() {
            return perPage;
        }

        public void setPerPage(Integer perPage) {
            this.perPage = perPage;
        }

        public String getPrevPageUrl() {
            return prevPageUrl;
        }

        public void setPrevPageUrl(String prevPageUrl) {
            this.prevPageUrl = prevPageUrl;
        }

        public Integer getTo() {
            return to;
        }

        public void setTo(Integer to) {
            this.to = to;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

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

        @SerializedName("thumbImage")
        @Expose
        private String thumbImage;

        @SerializedName("fullImage")
        @Expose
        private String fullImage;
        public String getThumbImage() {
            return thumbImage;
        }

        public void setThumbImage(String thumbImage) {
            this.thumbImage = thumbImage;
        }

        public String getFullImage() {
            return fullImage;
        }

        public void setFullImage(String fullImage) {
            this.fullImage = fullImage;
        }

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
    public class Category {

        @SerializedName("ecat_id")
        @Expose
        private Integer ecatId;
        @SerializedName("ecat_name")
        @Expose
        private String ecatName;
        @SerializedName("ecat_name_ar")
        @Expose
        private String ecatNameAr;
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

        public String getEcatNameAr() {
            return ecatNameAr;
        }

        public void setEcatNameAr(String ecatNameAr) {
            this.ecatNameAr = ecatNameAr;
        }

        public Integer getLangId() {
            return langId;
        }

        public void setLangId(Integer langId) {
            this.langId = langId;
        }

    }
}
