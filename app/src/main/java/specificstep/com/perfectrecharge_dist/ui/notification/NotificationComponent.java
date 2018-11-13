package specificstep.com.perfectrecharge_dist.ui.notification;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = NotificationModule.class)
public interface NotificationComponent {
    void inject(NotificationFragment fragment);
}
