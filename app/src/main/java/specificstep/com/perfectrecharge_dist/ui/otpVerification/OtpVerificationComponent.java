package specificstep.com.perfectrecharge_dist.ui.otpVerification;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = OtpVerificationPresenterModule.class)
public interface OtpVerificationComponent {
    void inject(OtpVerificationActivity activity);
}
