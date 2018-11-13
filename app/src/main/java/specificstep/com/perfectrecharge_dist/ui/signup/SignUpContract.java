package specificstep.com.perfectrecharge_dist.ui.signup;

import android.content.Context;

import specificstep.com.perfectrecharge_dist.ui.base.BasePresenter;
import specificstep.com.perfectrecharge_dist.ui.base.BaseView;

public interface SignUpContract {

    interface Presenter extends BasePresenter {

        void register(String userName);
    }

    interface View extends BaseView<Presenter> {

        void setProgressIndicator();

        boolean isActive();

        void showError(String errorMsg);

        Context context();

        void showInternetNotAvailableDialog();

        void hideProgressIndicator();

        void showErrorDialog(String errorMessage);

        void showVerifyRegistrationScreen(String userName);
    }

}
