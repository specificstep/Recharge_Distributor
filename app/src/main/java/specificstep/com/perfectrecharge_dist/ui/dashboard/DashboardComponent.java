package specificstep.com.perfectrecharge_dist.ui.dashboard;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(modules = DashboardModule.class, dependencies = ApplicationComponent.class)
public interface DashboardComponent {
    void inject(DashboardActivity activity);
}
