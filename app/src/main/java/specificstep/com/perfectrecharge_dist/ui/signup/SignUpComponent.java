package specificstep.com.perfectrecharge_dist.ui.signup;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = SignUpPresenterModule.class)
public interface SignUpComponent {

    void inject(SignUpActivity signUpActivity);
}

