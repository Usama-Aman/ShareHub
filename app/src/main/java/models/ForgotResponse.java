package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForgotResponse {

    @SerializedName("result")
    @Expose
    private List<Object> result = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("IsloggedIn")
    @Expose
    private Boolean isloggedIn;

    public List<Object> getResult() {
        return result;
    }

    public void setResult(List<Object> result) {
        this.result = result;
    }

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

    public Boolean getIsloggedIn() {
        return isloggedIn;
    }

    public void setIsloggedIn(Boolean isloggedIn) {
        this.isloggedIn = isloggedIn;
    }

}