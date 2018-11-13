package specificstep.com.perfectrecharge_dist.ui.forgotPassword;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = ForgotPasswordModule.class)
public interface ForgotPasswordComponent {
    void inject(ForgotPasswordActivity fragment);

}
