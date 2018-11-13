package specificstep.com.perfectrecharge_dist.ui.signIn;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFullScreenActivity;

public class SignInActivity extends BaseFullScreenActivity<SignInFragment> {

    public static final String EXTRA_USER_NAME = "user_name";

    @Inject
    SignInPresenter presenter;

    @Override
    public SignInFragment getFragmentContent() {
        return SignInFragment.getInstance(getIntent().hasExtra(EXTRA_USER_NAME)
                ? getIntent().getStringExtra(EXTRA_USER_NAME)
                : null);
    }

    @Override
    public void injectDependencies(SignInFragment fragment) {
        DaggerSignInComponent.builder()
                .applicationComponent(((AppController) getApplication()).getApplicationComponent())
                .signInPresenterModule(new SignInPresenterModule(fragment))
                .build().inject(this);
    }
}
