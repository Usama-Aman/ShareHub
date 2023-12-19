package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResult {

    @SerializedName("user_data")
    @Expose
    private UserModel userData;
    @SerializedName("token")
    @Expose
    private String token;

    public UserModel getUserData() {
        return userData;
    }

    public void setUserData(UserModel userData) {
        this.userData = userData;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}