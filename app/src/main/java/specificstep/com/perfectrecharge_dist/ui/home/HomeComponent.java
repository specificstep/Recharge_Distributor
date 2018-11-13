package specificstep.com.perfectrecharge_dist.ui.home;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = HomeModule.class)
public interface HomeComponent {

    void inject(HomeActivity homeActivity);
}
