package specificstep.com.perfectrecharge_dist.data.utils;

public enum ServiceType {
    MOBILE(1), DTH(2);
    int type;

    ServiceType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
