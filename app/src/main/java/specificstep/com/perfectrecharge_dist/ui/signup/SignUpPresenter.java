package specificstep.com.perfectrecharge_dist.ui.signup;

import android.util.Patterns;

import com.google.common.base.Strings;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.GlobalClasses.Constants;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.data.entity.BaseResponse;
import specificstep.com.perfectrecharge_dist.data.exceptions.InvalidUserNameException;
import specificstep.com.perfectrecharge_dist.exceptions.ErrorMessageFactory;
import specificstep.com.perfectrecharge_dist.interactors.DefaultObserver;
import specificstep.com.perfectrecharge_dist.interactors.exception.DefaultErrorBundle;
import specificstep.com.perfectrecharge_dist.interactors.usecases.SignUpUseCase;

class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;
    private final SignUpUseCase signUpUseCase;

    @Inject
    SignUpPresenter(SignUpContract.View view, SignUpUseCase signUpUseCase) {
        this.view = view;
        this.signUpUseCase = signUpUseCase;
    }

    @Inject
    void setupListeners() {
        view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        if (signUpUseCase != null) {
            signUpUseCase.dispose();
        }
    }

    @Override
    public void register(String userName) {
        if (userName.isEmpty()) {
            view.showError(view.context().getString(R.string.message_enter_username));
            return;
        } else if (Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
            view.showInternetNotAvailableDialog();
            return;
        }
        callSignUpAPI(userName);
    }

    private void callSignUpAPI(final String userName) {
        view.setProgressIndicator();
        signUpUseCase.execute(new DefaultObserver<BaseResponse>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                if(e instanceof InvalidUserNameException) {
                    showInValidUserNameError(userName);
                    return;
                }
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

            @Override
            public void onNext(BaseResponse value) {
                super.onNext(value);
                view.hideProgressIndicator();
                view.showVerifyRegistrationScreen(userName);
            }

        }, SignUpUseCase.Params.toParams(userName, Integer.parseInt(Constants.LOGIN_TYPE_DISTRIBUTER)));
    }

    private void showInValidUserNameError(String userName) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            view.showErrorDialog(view.context().getString(R.string.invalid_email_address));
        }else if(userName.matches("[0-9]+")) {
            view.showErrorDialog(view.context().getString(R.string.invalid_phone_number));
        }else {
            view.showErrorDialog(view.context().getString(R.string.invalid_username));
        }
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }
}
