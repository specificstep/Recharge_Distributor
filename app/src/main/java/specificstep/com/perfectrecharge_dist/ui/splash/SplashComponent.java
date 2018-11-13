package specificstep.com.perfectrecharge_dist.ui.splash;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = SplashModule.class)
public interface SplashComponent {

    void inject(SplashActivity splashActivity);
}
