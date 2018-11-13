package specificstep.com.perfectrecharge_dist.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CashSummaryResponse {
    @SerializedName("data")
    List<CashSummaryEntity> data;

    public List<CashSummaryEntity> getData() {
        return data;
    }
}
