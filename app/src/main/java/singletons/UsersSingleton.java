package singletons;

import java.util.List;

import models.GroupsModel;
import models.UserDetail;

public class UsersSingleton {

    private static UsersSingleton ourInstance = new UsersSingleton();

    public static UsersSingleton getInstance() {
        if (ourInstance == null) ourInstance = getSync();
        return ourInstance;
    }

    private static synchronized UsersSingleton getSync() {
        if (ourInstance == null) ourInstance = new UsersSingleton();
        return ourInstance;
    }

    private UsersSingleton() {
    }


    public static UsersSingleton getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(UsersSingleton ourInstance) {
        UsersSingleton.ourInstance = ourInstance;
    }

    private List<UserDetail> usersList;
    private List<GroupsModel> groupsList;

    public List<UserDetail> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<UserDetail> usersList) {
        this.usersList = usersList;
    }

    public List<GroupsModel> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(List<GroupsModel> groupsList) {
        this.groupsList = groupsList;
    }
}