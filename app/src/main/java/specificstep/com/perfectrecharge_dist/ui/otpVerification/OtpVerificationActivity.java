package specificstep.com.perfectrecharge_dist.ui.otpVerification;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFullScreenActivity;

public class OtpVerificationActivity extends BaseFullScreenActivity<OtpVerificationFragment> {

    public static final String EXTRA_USERNAME = "user_name";

    @Inject
    OtpVerificationPresenter presenter;

    @Override
    public OtpVerificationFragment getFragmentContent() {
        return OtpVerificationFragment.getInstance(getIntent().getStringExtra(EXTRA_USERNAME));
    }

    @Override
    public void injectDependencies(OtpVerificationFragment fragment) {
        DaggerOtpVerificationComponent.builder()
                .applicationComponent(((AppController) getApplication()).getApplicationComponent())
                .otpVerificationPresenterModule(new OtpVerificationPresenterModule(fragment))
                .build()
                .inject(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
