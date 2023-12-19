package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfileModel {

    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("followers_count")
    @Expose
    private Integer followersCount;
    @SerializedName("created_event")
    @Expose
    private List<CreatedEvent> createdEvent = null;
    @SerializedName("followers_created_event")
    @Expose
    private List<FollowersCreatedEvent> followersCreatedEvent = null;
    @SerializedName("joined_event")
    @Expose
    private List<JoinedEvent> joinedEvent = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public List<CreatedEvent> getCreatedEvent() {
        return createdEvent;
    }

    public void setCreatedEvent(List<CreatedEvent> createdEvent) {
        this.createdEvent = createdEvent;
    }

    public List<FollowersCreatedEvent> getFollowersCreatedEvent() {
        return followersCreatedEvent;
    }

    public void setFollowersCreatedEvent(List<FollowersCreatedEvent> followersCreatedEvent) {
        this.followersCreatedEvent = followersCreatedEvent;
    }

    public List<JoinedEvent> getJoinedEvent() {
        return joinedEvent;
    }

    public void setJoinedEvent(List<JoinedEvent> joinedEvent) {
        this.joinedEvent = joinedEvent;
    }

}
