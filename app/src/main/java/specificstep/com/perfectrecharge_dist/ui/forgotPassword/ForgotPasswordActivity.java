package specificstep.com.perfectrecharge_dist.ui.forgotPassword;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFullScreenActivity;

public class ForgotPasswordActivity extends BaseFullScreenActivity<ForgotPasswordFragment> {

   // public static final String EXTRA_USERNAME = "user_name";
   public static final String EXTRA_OTP = "key_otp";
    public static final String EXTRA_PASSWORD = "key_password";

    @Inject
    ForgotPasswordPresenter presenter;

    @Override
    public ForgotPasswordFragment getFragmentContent() {
        return ForgotPasswordFragment.getInstance(getIntent().getStringExtra(EXTRA_OTP),getIntent().getStringExtra(EXTRA_PASSWORD));
    }

    @Override
    public void injectDependencies(ForgotPasswordFragment fragment) {
        DaggerForgotPasswordComponent.builder()
                .applicationComponent(((AppController) getApplication()).getApplicationComponent())
                .forgotPasswordModule(new ForgotPasswordModule(fragment))
                .build()
                .inject(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
