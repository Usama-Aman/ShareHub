package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.List;

public class UserProfileResponseData {

    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("followers_count")
    @Expose
    private BigInteger followersCount;
    @SerializedName("created_event")
    @Expose
    private List<UserMyEventResponse> createdEvent = null;
    @SerializedName("followers_created_event")
    @Expose
    private List<UserFollowersEventResponse> followersCreatedEvent = null;
    @SerializedName("joined_event")
    @Expose
    private List<UserAttendingEventResponse> joinedEvent = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigInteger getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(BigInteger followersCount) {
        this.followersCount = followersCount;
    }

    public List<UserMyEventResponse> getCreatedEvent() {
        return createdEvent;
    }

    public void setCreatedEvent(List<UserMyEventResponse> createdEvent) {
        this.createdEvent = createdEvent;
    }

    public List<UserFollowersEventResponse> getFollowersCreatedEvent() {
        return followersCreatedEvent;
    }

    public void setFollowersCreatedEvent(List<UserFollowersEventResponse> followersCreatedEvent) {
        this.followersCreatedEvent = followersCreatedEvent;
    }

    public List<UserAttendingEventResponse> getJoinedEvent() {
        return joinedEvent;
    }

    public void setJoinedEvent(List<UserAttendingEventResponse> joinedEvent) {
        this.joinedEvent = joinedEvent;
    }

}