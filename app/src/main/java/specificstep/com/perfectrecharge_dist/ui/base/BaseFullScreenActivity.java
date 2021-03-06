package specificstep.com.perfectrecharge_dist.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import specificstep.com.perfectrecharge_dist.R;

public abstract class BaseFullScreenActivity<ContentFragment extends Fragment> extends BaseActivity {
    protected Integer getLayoutId() {
        return R.layout.activity_base_full_screen;
    }

    protected Integer getFragmentContainerId() {
        return R.id.fragment_container;
    }

    public abstract ContentFragment getFragmentContent();

    public abstract void injectDependencies(ContentFragment fragment);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment == null) {
            fragment = getFragmentContent();
            replaceFragment(getFragmentContainerId(), fragment);
        }
        try {
            injectDependencies((ContentFragment) fragment);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
