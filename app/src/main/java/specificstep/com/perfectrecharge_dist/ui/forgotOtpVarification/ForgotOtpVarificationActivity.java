package specificstep.com.perfectrecharge_dist.ui.forgotOtpVarification;

import javax.inject.Inject;
import dagger.Module;
import dagger.Provides;

import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFullScreenActivity;

public class ForgotOtpVarificationActivity extends BaseFullScreenActivity<ForgotOtpVerificationFragment> {


    public static final String EXTRA_USERNAME = "user_name";

    @Inject
    ForgotOtpVerificationPresenter presenter;

    @Override
    public ForgotOtpVerificationFragment getFragmentContent() {
        return ForgotOtpVerificationFragment.getInstance(getIntent().getStringExtra(EXTRA_USERNAME));
    }

    @Override
    public void injectDependencies(ForgotOtpVerificationFragment fragment) {
        DaggerForgotOtpVerificationComponent.builder()
                .forgotOtpVerificationPresenterModule(new ForgotOtpVerificationPresenterModule(fragment))
                .applicationComponent(((AppController) getApplication()).getApplicationComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
