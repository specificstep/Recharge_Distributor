package specificstep.com.perfectrecharge_dist.ui.home;

import android.support.annotation.IntDef;

import static specificstep.com.perfectrecharge_dist.ui.home.Flow.ADD_BALANCE;
import static specificstep.com.perfectrecharge_dist.ui.home.Flow.ADD_USER;
import static specificstep.com.perfectrecharge_dist.ui.home.Flow.CASHBOOK;
import static specificstep.com.perfectrecharge_dist.ui.home.Flow.CASH_SUMMARY;
import static specificstep.com.perfectrecharge_dist.ui.home.Flow.CHANGE_PASSWORD;
import static specificstep.com.perfectrecharge_dist.ui.home.Flow.NOTIFICATION;
import static specificstep.com.perfectrecharge_dist.ui.home.Flow.PARENT_USER;
import static specificstep.com.perfectrecharge_dist.ui.home.Flow.PURCHASE_USER;
import static specificstep.com.perfectrecharge_dist.ui.home.Flow.UPDATE;
import static specificstep.com.perfectrecharge_dist.ui.home.Flow.USER_LIST;

@IntDef({ADD_BALANCE, CASH_SUMMARY, CASHBOOK, PURCHASE_USER, ADD_USER,UPDATE,
        CHANGE_PASSWORD, NOTIFICATION,
        USER_LIST, PARENT_USER })
public @interface Flow {
    int ADD_BALANCE = 0;
    int CASH_SUMMARY = 1;
    int CASHBOOK = 2;
    int PURCHASE_USER = 3;
    int ADD_USER = 4;
    int UPDATE = 5;
    int CHANGE_PASSWORD = 6;
    int NOTIFICATION = 7;
    int USER_LIST = 8;
    int PARENT_USER = 9;

    //int AC_LEDGER = 10;
}
