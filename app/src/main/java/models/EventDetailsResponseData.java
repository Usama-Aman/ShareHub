package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventDetailsResponseData {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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




    public class Media {

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

    }

    public class Data {

        @SerializedName("event")
        @Expose
        private Event event;
        @SerializedName("media")
        @Expose
        private List<Media> media = null;
        @SerializedName("participants_count")
        @Expose
        private Integer participantsCount;
        @SerializedName("not_participants_count")
        @Expose
        private Integer notParticipantsCount;

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        public List<Media> getMedia() {
            return media;
        }

        public void setMedia(List<Media> media) {
            this.media = media;
        }

        public Integer getParticipantsCount() {
            return participantsCount;
        }

        public void setParticipantsCount(Integer participantsCount) {
            this.participantsCount = participantsCount;
        }

        public Integer getNotParticipantsCount() {
            return notParticipantsCount;
        }

        public void setNotParticipantsCount(Integer notParticipantsCount) {
            this.notParticipantsCount = notParticipantsCount;
        }

    }

}