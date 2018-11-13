package specificstep.com.perfectrecharge_dist.data.entity;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("status")
    int status = -1;
    @SerializedName("msg")
    String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
