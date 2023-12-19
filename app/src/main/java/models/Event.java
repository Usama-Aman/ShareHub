package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event implements Parcelable {

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
    private Integer eventPincode;
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
    @SerializedName("user_fullname")
    @Expose
    private String userFullname;
    @SerializedName("user_photo")
    @Expose
    private String userPhoto;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("fullImage")
    @Expose
    private String fullImage;
    @SerializedName("thumbImage")
    @Expose
    private String thumbImage;

    @SerializedName("isAttend")
    @Expose
    private int isAttend;

    @SerializedName("isLive")
    @Expose
    private int isLive;

    @SerializedName("isApproved")
    @Expose
    private int isApproved;
    @SerializedName("qrfullImage")
    @Expose
    private String qrfullImage;

    protected Event(Parcel in) {
        if (in.readByte() == 0) {
            eventId = null;
        } else {
            eventId = in.readInt();
        }
        eventTitle = in.readString();
        eventDescription = in.readString();
        eventStartDate = in.readString();
        eventStartTime = in.readString();
        eventEndDate = in.readString();
        eventEndTime = in.readString();
        eventCoverphoto = in.readString();
        eventLocation = in.readString();
        eventVenueLat = in.readString();
        eventVenueLong = in.readString();
        eventDetails = in.readString();
        if (in.readByte() == 0) {
            ecatId = null;
        } else {
            ecatId = in.readInt();
        }
        if (in.readByte() == 0) {
            eventIscommentsAllowed = null;
        } else {
            eventIscommentsAllowed = in.readInt();
        }
        if (in.readByte() == 0) {
            eventIsprivate = null;
        } else {
            eventIsprivate = in.readInt();
        }
        if (in.readByte() == 0) {
            eventIsPincodeRequired = null;
        } else {
            eventIsPincodeRequired = in.readInt();
        }
        if (in.readByte() == 0) {
            eventPincode = null;
        } else {
            eventPincode = in.readInt();
        }
        eventDateadded = in.readString();
        if (in.readByte() == 0) {
            eventCountParticipants = null;
        } else {
            eventCountParticipants = in.readInt();
        }
        eventLink = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        if (in.readByte() == 0) {
            eventIsApprovalRequired = null;
        } else {
            eventIsApprovalRequired = in.readInt();
        }
        if (in.readByte() == 0) {
            eventIsApproved = null;
        } else {
            eventIsApproved = in.readInt();
        }
        if (in.readByte() == 0) {
            eventCountComments = null;
        } else {
            eventCountComments = in.readInt();
        }
        userFullname = in.readString();
        userPhoto = in.readString();
        email = in.readString();
        fullImage = in.readString();
        thumbImage = in.readString();
        isAttend = in.readInt();
        isLive = in.readInt();
        qrfullImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (eventId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventId);
        }
        dest.writeString(eventTitle);
        dest.writeString(eventDescription);
        dest.writeString(eventStartDate);
        dest.writeString(eventStartTime);
        dest.writeString(eventEndDate);
        dest.writeString(eventEndTime);
        dest.writeString(eventCoverphoto);
        dest.writeString(eventLocation);
        dest.writeString(eventVenueLat);
        dest.writeString(eventVenueLong);
        dest.writeString(eventDetails);
        if (ecatId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(ecatId);
        }
        if (eventIscommentsAllowed == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventIscommentsAllowed);
        }
        if (eventIsprivate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventIsprivate);
        }
        if (eventIsPincodeRequired == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventIsPincodeRequired);
        }
        if (eventPincode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventPincode);
        }
        dest.writeString(eventDateadded);
        if (eventCountParticipants == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventCountParticipants);
        }
        dest.writeString(eventLink);
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        if (eventIsApprovalRequired == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventIsApprovalRequired);
        }
        if (eventIsApproved == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventIsApproved);
        }
        if (eventCountComments == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventCountComments);
        }
        dest.writeString(userFullname);
        dest.writeString(userPhoto);
        dest.writeString(email);
        dest.writeString(fullImage);
        dest.writeString(thumbImage);
        dest.writeInt(isAttend);
        dest.writeInt(isLive);
        dest.writeString(qrfullImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public int getIsAttend() {
        return isAttend;
    }

    public void setIsAttend(int isAttend) {
        this.isAttend = isAttend;
    }

    public int getIsLive() {
        return isLive;
    }

    public void setIsLive(int isLive) {
        this.isLive = isLive;
    }

    public String getQrfullImage() {
        return qrfullImage;
    }

    public void setQrfullImage(String qrfullImage) {
        this.qrfullImage = qrfullImage;
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

    public Integer getEventPincode() {
        return eventPincode;
    }

    public void setEventPincode(Integer eventPincode) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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