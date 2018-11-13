package specificstep.com.perfectrecharge_dist.ui.splash;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFullScreenActivity;

public class SplashActivity extends BaseFullScreenActivity<SplashFragment> {

    @Inject
    SplashPresenter presenter;

    @Override
    public SplashFragment getFragmentContent() {
        return new SplashFragment();
    }

    @Override
    public void injectDependencies(SplashFragment fragment) {
        DaggerSplashComponent.builder()
                .applicationComponent(((AppController)getApplication()).getApplicationComponent())
                .splashModule(new SplashModule(fragment))
                .build().inject(this);
    }
}
