package specificstep.com.perfectrecharge_dist.ui.updateData;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = UpdateDataModule.class)
public interface UpdateDataComponent {
    void inject(UpdateDataFragment fragment);
}
