package specificstep.com.perfectrecharge_dist.ui.parentUser;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = ParentUserModule.class)
public interface ParentUserComponent {
    void inject(ParentUserFragment fragment);
}
