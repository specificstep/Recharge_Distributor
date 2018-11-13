package specificstep.com.perfectrecharge_dist.ui.otpVerification;


import android.os.CountDownTimer;
import android.util.Log;

import com.google.common.base.Strings;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.GlobalClasses.Constants;
import specificstep.com.perfectrecharge_dist.Models.Color;
import specificstep.com.perfectrecharge_dist.Models.Company;
import specificstep.com.perfectrecharge_dist.Models.Product;
import specificstep.com.perfectrecharge_dist.Models.State;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.data.entity.BaseResponse;
import specificstep.com.perfectrecharge_dist.data.utils.ServiceType;
import specificstep.com.perfectrecharge_dist.exceptions.ErrorMessageFactory;
import specificstep.com.perfectrecharge_dist.interactors.DefaultObserver;
import specificstep.com.perfectrecharge_dist.interactors.exception.DefaultErrorBundle;
import specificstep.com.perfectrecharge_dist.interactors.usecases.GetCompanyUseCase;
import specificstep.com.perfectrecharge_dist.interactors.usecases.GetProductUseCase;
import specificstep.com.perfectrecharge_dist.interactors.usecases.GetSettingsUseCase;
import specificstep.com.perfectrecharge_dist.interactors.usecases.GetStateUseCase;
import specificstep.com.perfectrecharge_dist.interactors.usecases.OtpVerifyUseCase;
import specificstep.com.perfectrecharge_dist.interactors.usecases.SignUpUseCase;

class OtpVerificationPresenter implements OtpVerificationContract.Presenter {

    private static final long TIMER_INTERVAL = 1000;
    private static final long TIMER_DURATION = 1000 * 60 * 2;
    private OtpVerificationContract.View view;
    private boolean isOtpReceived;
    private final SignUpUseCase signUpUseCase;
    private final OtpVerifyUseCase otpVerifyUseCase;
    private final GetCompanyUseCase getCompanyUseCase;
    private final GetProductUseCase getProductUseCase;
    private final GetStateUseCase getStateUseCase;
    private final GetSettingsUseCase getSettingsUseCase;

    private CountDownTimer countDownTimer = new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {
        @Override
        public void onTick(long durationInMillis) {
            long seconds = durationInMillis / 1000;
            String timeFormat = String.format(Locale.getDefault(), "%02d:%02d",
                    (seconds % 3600) / 60, (seconds % 60));
            Log.d(getClass().getSimpleName(), "Remaining time: " + timeFormat);
            view.updateCountDownTime(timeFormat);
        }

        @Override
        public void onFinish() {
            onCountDownFinished();
        }
    };

    @Inject
    public OtpVerificationPresenter(OtpVerificationContract.View view,
                                    SignUpUseCase signUpUseCase,
                                    GetCompanyUseCase getCompanyUseCase,
                                    GetProductUseCase getProductUseCase,
                                    GetStateUseCase getStateUseCase,
                                    GetSettingsUseCase getSettingsUseCase,
                                    OtpVerifyUseCase otpVerifyUseCase) {
        this.view = view;
        this.signUpUseCase = signUpUseCase;
        this.otpVerifyUseCase = otpVerifyUseCase;
        this.getCompanyUseCase = getCompanyUseCase;
        this.getProductUseCase = getProductUseCase;
        this.getStateUseCase = getStateUseCase;
        this.getSettingsUseCase = getSettingsUseCase;
    }

    @Inject
    void setupListeners() {
        view.setPresenter(this);
    }

    private void onCountDownFinished() {
        view.hideCountDownTimer();
        view.enableResendButton();
//        view.disableOtpEditText();
    }

    @Override
    public void start() {
        if (!isOtpReceived) {
            view.registerForSmsReceiver();
        }
        startCountDownTimer();
    }

    @Override
    public void stop() {
        view.unRegisterSmsReceiver();
        countDownTimer.cancel();
    }

    @Override
    public void destroy() {
        countDownTimer.cancel();
        countDownTimer = null;

        signUpUseCase.dispose();
        otpVerifyUseCase.dispose();
        getCompanyUseCase.dispose();
        getProductUseCase.dispose();
        getStateUseCase.dispose();
        getSettingsUseCase.dispose();

    }

    @Override
    public void onOtpSmsReceived(String otp) {
        isOtpReceived = true;
        view.fillOtp(otp);
        view.unRegisterSmsReceiver();
        if (!otp.isEmpty()) {
            verifyOtp();
        }

    }

    private void verifyOtp() {
        String otp = view.getOtp();
        if (Strings.isNullOrEmpty(otp)) {
            view.showErrorMessage(view.context().getString(R.string.enter_opt));
            return;
        }
        callVerifyOtpAPI(otp, view.getUserName());
    }

    private void callVerifyOtpAPI(String otp, String userName) {
        view.setProgressIndicator();
        otpVerifyUseCase.execute(new DefaultObserver<BaseResponse>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

            @Override
            public void onNext(BaseResponse value) {
                super.onNext(value);
                onOtpVerificationCompleted();
            }

        }, OtpVerifyUseCase.Params.toParams(userName, otp, Integer.parseInt(Constants.LOGIN_TYPE_DISTRIBUTER)));
    }

    private void onOtpVerificationCompleted() {
        getMobileCompany();
    }

    private void getMobileCompany() {
        getCompanyUseCase.execute(new DefaultObserver<List<Company>>() {

            @Override
            public void onNext(List<Company> value) {
                super.onNext(value);
                getDTHCompany();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetCompanyUseCase.Params.toParams(ServiceType.MOBILE.getType()));
    }

    private void getDTHCompany() {
        getCompanyUseCase.execute(new DefaultObserver<List<Company>>() {

            @Override
            public void onNext(List<Company> value) {
                super.onNext(value);
                getMobileProducts();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetCompanyUseCase.Params.toParams(ServiceType.DTH.getType()));
    }

    private void getMobileProducts() {
        getProductUseCase.execute(new DefaultObserver<List<Product>>() {

            @Override
            public void onNext(List<Product> value) {
                super.onNext(value);
                getDTHProducts();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetProductUseCase.Params.toParams(ServiceType.MOBILE.getType()));
    }

    private void getDTHProducts() {
        getProductUseCase.execute(new DefaultObserver<List<Product>>() {

            @Override
            public void onNext(List<Product> value) {
                super.onNext(value);
                getStates();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetProductUseCase.Params.toParams(ServiceType.DTH.getType()));
    }

    private void getStates() {
        getStateUseCase.execute(new DefaultObserver<List<State>>() {

            @Override
            public void onNext(List<State> value) {
                super.onNext(value);
                getSettings();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetStateUseCase.Params.toParams(ServiceType.MOBILE.getType()));
    }

    private void getSettings() {
        getSettingsUseCase.execute(new DefaultObserver<List<Color>>() {

            @Override
            public void onNext(List<Color> value) {
                super.onNext(value);
                view.hideProgressIndicator();
                view.showLoginScreen(view.getUserName());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, null);
    }

    @Override
    public void onVerifyOtpButtonClicked() {
        verifyOtp();
    }

    @Override
    public void onResendOtpButtonClicked() {
        callSignUpAPI(view.getUserName());
    }

    private void callSignUpAPI(final String userName) {
        view.setProgressIndicator();
        signUpUseCase.execute(new DefaultObserver<BaseResponse>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

            @Override
            public void onNext(BaseResponse value) {
                super.onNext(value);
                view.hideProgressIndicator();
                onSignUpSuccess();
            }

        }, SignUpUseCase.Params.toParams(userName, Integer.parseInt(Constants.LOGIN_TYPE_DISTRIBUTER)));
    }

    private void onSignUpSuccess() {
//        view.enableOtpEditText();
        view.disableResendOtpButton();
        startCountDownTimer();
    }

    private void startCountDownTimer() {
        countDownTimer.start();
        view.showCountDownTimer();
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }
}
