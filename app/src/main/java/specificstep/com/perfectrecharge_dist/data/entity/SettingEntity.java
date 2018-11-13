package specificstep.com.perfectrecharge_dist.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SettingEntity {
    @SerializedName("color")
    List<ColorEntity> colors;

    public List<ColorEntity> getColors() {
        return colors;
    }
}
