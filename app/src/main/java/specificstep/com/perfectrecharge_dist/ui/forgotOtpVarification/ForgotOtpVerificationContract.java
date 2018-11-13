package specificstep.com.perfectrecharge_dist.ui.forgotOtpVarification;

import android.content.Context;

import specificstep.com.perfectrecharge_dist.ui.base.BasePresenter;
import specificstep.com.perfectrecharge_dist.ui.base.BaseView;

public interface ForgotOtpVerificationContract {

    interface Presenter extends BasePresenter {

        void onOtpSmsReceived(String otp,Context context);

        void onVerifyOtpButtonClicked(Context context, String forgot_otp);

        void onResendOtpButtonClicked();
    }

    interface View extends BaseView<ForgotOtpVerificationContract.Presenter> {

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

        void showForgotPasswordScreen(String userName,String password);
    }

}
