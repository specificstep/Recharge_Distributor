package specificstep.com.perfectrecharge_dist.ui.splash;

import android.content.Context;
import android.support.annotation.StringRes;

import specificstep.com.perfectrecharge_dist.ui.base.BasePresenter;
import specificstep.com.perfectrecharge_dist.ui.base.BaseView;

public interface SplashContract {

    interface Presenter extends BasePresenter {

        void initialize();

        void onTimeoutCompleted();
    }

    interface View extends BaseView<Presenter> {

        void scheduleTimeout(long milliSeconds);

        void startMainScreen();

        void startLoginScreen();

        void startSignUpScreen();

        Context context();

        void createFirstTimeNotification();

        void showErrorDialog(@StringRes int strResId);
    }
}
