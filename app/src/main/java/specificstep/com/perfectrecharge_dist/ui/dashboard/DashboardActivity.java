package specificstep.com.perfectrecharge_dist.ui.dashboard;

import android.os.Bundle;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.ui.base.ToolBarActivity;

public class DashboardActivity extends ToolBarActivity<DashboardFragment> {

    @Inject
    DashboardPresenter presenter;

    @Override
    public DashboardFragment getFragmentContent() {
        return new DashboardFragment();
    }

    @Override
    public void injectDependencies(DashboardFragment fragment) {
        DaggerDashboardComponent.builder()
                .applicationComponent(((AppController)getApplication()).getApplicationComponent())
                .dashboardModule(new DashboardModule(fragment))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setLogo(R.drawable.actionbar_logo_with_space);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
    }
}
