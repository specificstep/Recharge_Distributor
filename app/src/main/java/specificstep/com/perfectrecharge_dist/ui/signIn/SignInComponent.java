package specificstep.com.perfectrecharge_dist.ui.signIn;


import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = SignInPresenterModule.class)
public interface SignInComponent {

    void inject(SignInActivity signInActivity);
}
