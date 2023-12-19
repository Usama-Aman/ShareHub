package models;

import java.util.List;

public class UserSingleton {
    private static UserSingleton ourInstance = new UserSingleton();

    public static UserSingleton getInstance() {
        if (ourInstance == null) ourInstance = getSync();
        return ourInstance;
    }

    private static synchronized UserSingleton getSync() {
        if (ourInstance == null) ourInstance = new UserSingleton();
        return ourInstance;
    }

    private UserSingleton() {
    }

    private List<User> usersList;
    private List<EventMediaResponseDataDetail> mediaList;



    public static UserSingleton getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(UserSingleton ourInstance) {
        UserSingleton.ourInstance = ourInstance;
    }

    public List<User> getUserList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }



    public List<EventMediaResponseDataDetail> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<EventMediaResponseDataDetail> mediaList) {
        this.mediaList = mediaList;
    }

}