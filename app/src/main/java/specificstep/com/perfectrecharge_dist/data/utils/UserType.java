package specificstep.com.perfectrecharge_dist.data.utils;

public enum UserType {
    DISTRIBUTOR(2), RETAILER(4), RESELLER(3), SELF(0);
    int type;

    UserType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


}
