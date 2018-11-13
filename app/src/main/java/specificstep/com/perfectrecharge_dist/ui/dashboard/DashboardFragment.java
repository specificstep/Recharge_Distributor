package specificstep.com.perfectrecharge_dist.ui.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.ui.addUser.AddUserFragment;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFragment;
import specificstep.com.perfectrecharge_dist.ui.home.Flow;
import specificstep.com.perfectrecharge_dist.ui.home.HomeActivity;
import specificstep.com.perfectrecharge_dist.ui.signIn.SignInActivity;
import specificstep.com.perfectrecharge_dist.utility.Utility;

public class DashboardFragment extends BaseFragment implements DashboardContract.View {

    @BindView(R.id.notification_badge)
    TextView notificationBadgeTextView;


    @BindView(R.id.ac_ledger_view)
    FrameLayout ac_ledger_view;


    private DashboardContract.Presenter presenter;
    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.onNotificationRefreshed();
        }
    };
    private MenuItem balanceMenuItem;

    @Override
    public void setPresenter(DashboardContract.Presenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onStop() {
        presenter.stop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);

        ac_ledger_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // HomeActivity mActivity = new HomeActivity();
              //  (HomeActivity)getActivity().showAcLedgerFragment();

                Intent i = new Intent(getActivity(),HomeActivity.class);
                i.putExtra("from_last","dashbordfrag_to_acledgerfrag");
                startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.initialize();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_balance, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        balanceMenuItem = menu.findItem(R.id.action_balance_menu_main);
    }

    @OnClick({R.id.add_recharge_view, R.id.user_list_view, R.id.search_transaction_view,
            R.id.update_button, R.id.change_password_button, R.id.notification_button,
            R.id.logout_button ,
           R.id.add_user_view,
           R.id.pourchase_user_view})

    void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_recharge_view:
                presenter.onAddRechargeButtonClicked();
                break;

            case R.id.ac_ledger_view:
                /*HomeActivity mActivity = new HomeActivity();
                mActivity.showAcLedgerFragment();
                break;*/
                Intent i1 = new Intent(getActivity(),HomeActivity.class);
                i1.putExtra("from_last","dashbordfrag_to_acledgerfrag");
                startActivity(i1);
                break;

            case R.id.add_user_view:
                //presenter.onListUserButtonClicked();
                Intent i = new Intent(getActivity(),HomeActivity.class);
                i.putExtra("from_last","dashbordfrag_to_adduserfrag");
                startActivity(i);
                break;
            case R.id.pourchase_user_view:
                Intent i2 = new Intent(getActivity(),HomeActivity.class);
                i2.putExtra("from_last","dashbordfrag_to_purchasefrag");
                startActivity(i2);
                break;
            case R.id.user_list_view:
                presenter.onListUserButtonClicked();
                break;
            case R.id.search_transaction_view:
                presenter.onSearchTransactionButtonClicked();
                break;
            case R.id.update_button:
                presenter.onUpdateButtonClicked();
                break;
            case R.id.change_password_button:
                presenter.onChangePasswordButtonClicked();
                break;
            case R.id.notification_button:
                presenter.onNotificationButtonClicked();
                break;
            case R.id.logout_button:
                presenter.onLogoutButtonClicked();
                break;
        }
    }

    @Override
    public void showMainScreen(@Flow int update) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_FLOW, update);
        startActivity(intent);
    }

    @Override
    public void showAutoUpdateScreen(@Flow int update) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_FLOW, update);
        intent.putExtra(HomeActivity.EXTRA_AUTO_UPDATE, true);
        startActivity(intent);
    }

    @Override
    public void showLoginScreen() {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void registerNotificationReceiver(String action) {
        IntentFilter intentFilter = new IntentFilter(action);
        getActivity().registerReceiver(mNotificationReceiver, intentFilter);
    }

    @Override
    public void unRegisterNotificationReceiver() {
        getActivity().unregisterReceiver(mNotificationReceiver);
    }

    @Override
    public void updateNotificationCount(int notificationCount) {
        notificationBadgeTextView.setText(String.valueOf(notificationCount));
    }

    @Override
    public void updateNotificationBadgeVisibility(boolean visible) {
        notificationBadgeTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBalanceInMenu(BigDecimal amount) {
        if(balanceMenuItem != null) {
            balanceMenuItem.setTitleCondensed(getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(amount)));
        }
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

}
