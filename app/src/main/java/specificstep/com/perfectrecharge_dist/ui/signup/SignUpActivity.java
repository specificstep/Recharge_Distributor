package specificstep.com.perfectrecharge_dist.ui.signup;

import android.os.Bundle;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.ui.base.ToolBarActivity;

public class SignUpActivity extends ToolBarActivity<SignUpFragment> {

    @Inject
    SignUpPresenter signUpPresenter;

    @Override
    public SignUpFragment getFragmentContent() {
        return new SignUpFragment();
    }

    @Override
    public void injectDependencies(SignUpFragment fragment) {

        DaggerSignUpComponent.builder()
                .applicationComponent(((AppController) getApplication()).getApplicationComponent())
                .signUpPresenterModule(new SignUpPresenterModule(fragment))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setLogo(R.drawable.actionbar_logo_with_space);
        toolbar.setTitle(R.string.register_app_title);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
