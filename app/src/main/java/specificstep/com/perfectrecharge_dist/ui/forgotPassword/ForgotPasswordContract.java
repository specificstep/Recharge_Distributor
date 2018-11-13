package specificstep.com.perfectrecharge_dist.ui.forgotPassword;

import android.content.Context;
import android.support.annotation.DrawableRes;

import specificstep.com.perfectrecharge_dist.ui.base.BasePresenter;

public interface ForgotPasswordContract {

    interface Presenter extends BasePresenter {

        void initialize();

        void onChangePasswordButtonClicked(Context context,String forgot_otp, String password, String oldPassword);

        void toggleOldPasswordVisibility();

        void toggleNewPasswordVisibility();

        void toggleConfirmPasswordVisibility();

        void onOldPasswordTextChanged(CharSequence text);

        void onNewPasswordTextChanged(CharSequence text);

        void onConfirmPasswordTextChanged(CharSequence text);
    }

    interface View {

        void setOldPassword(String oldPassword);

        void setPresenter(Presenter presenter);

        void showShowHideOldPasswordImageView();

        void hideShowHideOldPasswordImageView();

        void hideOldPassword();

        void showOldPassword();

        void hideNewPassword();

        void hideShowHideNewPasswordImageView();

        void showNewPassword();

        void showShowHideNewPasswordImageView();

        void hideConfirmPassword();

        void hideShowHideConfirmPasswordImageView();

        void showConfirmPassword();

        void showShowHideConfirmPasswordImageView();

        void showOldPasswordIcon(@DrawableRes int id);

        void showNewPasswordIcon(@DrawableRes int id);

        void showConfirmPasswordIcon(@DrawableRes int id);

        String getConfirmPassword();

        String getNewPassword();

        String getOldPassword();

        void showError(String error);

        Context context();

        void showProgressIndicator();

        void hideProgressIndicator();

        void showErrorDialog(String errorMessage);

        void showLoginScreen();

        void updateToggleOldPasswordIcon(@DrawableRes int drawableRes);

        void updateToggleConfirmPasswordIcon(@DrawableRes int drawableRes);

        void updateToggleNewPasswordIcon(@DrawableRes int drawableRes);

        void showInvalidAccessTokenPopup();
    }

}
