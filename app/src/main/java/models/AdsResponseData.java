package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsResponseData {



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

        @SerializedName("ead_id")
        @Expose
        private Integer eadId;
        @SerializedName("ead_name")
        @Expose
        private String eadName;
        @SerializedName("ead_image")
        @Expose
        private String eadImage;
        @SerializedName("event_id")
        @Expose
        private Integer eventId;
        @SerializedName("valid_from")
        @Expose
        private String validFrom;
        @SerializedName("valid_to")
        @Expose
        private String validTo;
        @SerializedName("fullImage")
        @Expose
        private String fullImage;
        @SerializedName("event_detail")
        @Expose
        private EventDetail eventDetail;

        public Integer getEadId() {
            return eadId;
        }

        public void setEadId(Integer eadId) {
            this.eadId = eadId;
        }

        public String getEadName() {
            return eadName;
        }

        public void setEadName(String eadName) {
            this.eadName = eadName;
        }

        public String getEadImage() {
            return eadImage;
        }

        public void setEadImage(String eadImage) {
            this.eadImage = eadImage;
        }

        public Integer getEventId() {
            return eventId;
        }

        public void setEventId(Integer eventId) {
            this.eventId = eventId;
        }

        public String getValidFrom() {
            return validFrom;
        }

        public void setValidFrom(String validFrom) {
            this.validFrom = validFrom;
        }

        public String getValidTo() {
            return validTo;
        }

        public void setValidTo(String validTo) {
            this.validTo = validTo;
        }

        public String getFullImage() {
            return fullImage;
        }

        public void setFullImage(String fullImage) {
            this.fullImage = fullImage;
        }

        public EventDetail getEventDetail() {
            return eventDetail;
        }

        public void setEventDetail(EventDetail eventDetail) {
            this.eventDetail = eventDetail;
        }
        public class EventDetail {

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
            @SerializedName("deleted_at")
            @Expose
            private Object deletedAt;
            @SerializedName("distance")
            @Expose
            private String distance;
            @SerializedName("fullImage")
            @Expose
            private String fullImage;
            @SerializedName("thumbImage")
            @Expose
            private String thumbImage;
            @SerializedName("qrfullImage")
            @Expose
            private String qrfullImage;

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

            public Object getDeletedAt() {
                return deletedAt;
            }

            public void setDeletedAt(Object deletedAt) {
                this.deletedAt = deletedAt;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
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

            public String getQrfullImage() {
                return qrfullImage;
            }

            public void setQrfullImage(String qrfullImage) {
                this.qrfullImage = qrfullImage;
            }

        }
    }


    }
