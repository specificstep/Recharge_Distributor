package specificstep.com.perfectrecharge_dist.ui.changePassword;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = ChangePasswordModule.class)
public interface ChangePasswordComponent {
    void inject(ChangePasswordFragment fragment);
}
