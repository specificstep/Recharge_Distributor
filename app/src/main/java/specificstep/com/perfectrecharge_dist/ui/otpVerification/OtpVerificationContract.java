package specificstep.com.perfectrecharge_dist.ui.otpVerification;

import android.content.Context;

import specificstep.com.perfectrecharge_dist.ui.base.BasePresenter;
import specificstep.com.perfectrecharge_dist.ui.base.BaseView;


public interface OtpVerificationContract {
    interface Presenter extends BasePresenter {

        void onOtpSmsReceived(String otp);

        void onVerifyOtpButtonClicked();

        void onResendOtpButtonClicked();
    }

    interface View extends BaseView<OtpVerificationContract.Presenter> {

        void fillOtp(String otp);

        void registerForSmsReceiver();

        void unRegisterSmsReceiver();

        String getUserName();

        void setProgressIndicator();

        void hideProgressIndicator();

        void enableOtpEditText();

        void disableResendOtpButton();

        Context context();

        void showErrorDialog(String errorMessage);

        void hideCountDownTimer();

        void enableResendButton();

        void disableOtpEditText();

        void showCountDownTimer();

        String getOtp();

        void showErrorMessage(String errorMessage);

        void updateCountDownTime(String timeFormat);

        void showLoginScreen(String userName);
    }
}
