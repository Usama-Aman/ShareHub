package models;

import java.util.List;

/**
 * Created by M.Mubashir on 5/24/2018.
 */

public class UserListSingleton {
    private static UserListSingleton ourInstance = new UserListSingleton();

    public static UserListSingleton getInstance() {
        if (ourInstance == null) ourInstance = getSync();
        return ourInstance;
    }

    private static synchronized UserListSingleton getSync() {
        if (ourInstance == null) ourInstance = new UserListSingleton();
        return ourInstance;
    }

    private UserListSingleton() {
    }

    private List<PeopleDataResponse.User> usersList;


    public static UserListSingleton getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(UserListSingleton ourInstance) {
        UserListSingleton.ourInstance = ourInstance;
    }

    public List<PeopleDataResponse.User> getUserList() {
        return usersList;
    }

    public void setUsersList(List<PeopleDataResponse.User> usersList) {
        this.usersList = usersList;
    }


}