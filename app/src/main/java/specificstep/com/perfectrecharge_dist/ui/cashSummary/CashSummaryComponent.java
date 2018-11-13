package specificstep.com.perfectrecharge_dist.ui.cashSummary;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = CashSummaryModule.class)
public interface CashSummaryComponent {
    void inject(CashSummaryFragment fragment);
}
