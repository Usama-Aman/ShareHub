package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentPageResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("data")
    @Expose
    private ContentPageResponseData data;

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

    public ContentPageResponseData getData() {
        return data;
    }

    public void setData(ContentPageResponseData data) {
        this.data = data;
    }

}
