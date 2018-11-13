package specificstep.com.perfectrecharge_dist.ui.addBalance;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = AddBalanceModule.class)
public interface AddBalanceComponent {
    void inject(AddBalanceFragment fragment);
}
