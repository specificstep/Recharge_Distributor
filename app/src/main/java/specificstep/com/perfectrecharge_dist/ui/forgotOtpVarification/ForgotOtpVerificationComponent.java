package specificstep.com.perfectrecharge_dist.ui.forgotOtpVarification;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;
import specificstep.com.perfectrecharge_dist.ui.otpVerification.OtpVerificationActivity;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = ForgotOtpVerificationPresenterModule.class)

public interface ForgotOtpVerificationComponent {
    void inject(ForgotOtpVarificationActivity activity);
}
