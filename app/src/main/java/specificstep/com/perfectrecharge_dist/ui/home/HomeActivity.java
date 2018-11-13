package specificstep.com.perfectrecharge_dist.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import specificstep.com.perfectrecharge_dist.Database.DatabaseHelper;
import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.GlobalClasses.Constants;
import specificstep.com.perfectrecharge_dist.GlobalClasses.MCrypt;
import specificstep.com.perfectrecharge_dist.GlobalClasses.URL;
import specificstep.com.perfectrecharge_dist.Models.AddUserTypeModel;
import specificstep.com.perfectrecharge_dist.Models.ChildUserModel;
import specificstep.com.perfectrecharge_dist.Models.Default;
import specificstep.com.perfectrecharge_dist.Models.User;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.ui.AcLedger.AcLedgerFragment;
import specificstep.com.perfectrecharge_dist.ui.PurchaseUser.PurchaseUserFragment;
import specificstep.com.perfectrecharge_dist.ui.addBalance.AddBalanceFragment;
import specificstep.com.perfectrecharge_dist.ui.addUser.AddUserFragment;
import specificstep.com.perfectrecharge_dist.ui.base.BaseActivity;
import specificstep.com.perfectrecharge_dist.ui.cashSummary.CashSummaryFragment;
import specificstep.com.perfectrecharge_dist.ui.changePassword.ChangePasswordFragment;
import specificstep.com.perfectrecharge_dist.ui.notification.NotificationFragment;
import specificstep.com.perfectrecharge_dist.ui.parentUser.ParentUserFragment;
import specificstep.com.perfectrecharge_dist.ui.updateData.UpdateDataFragment;
import specificstep.com.perfectrecharge_dist.ui.userList.UserListFragment;
import specificstep.com.perfectrecharge_dist.utility.InternetUtil;
import specificstep.com.perfectrecharge_dist.utility.Utility;

public class HomeActivity extends BaseActivity implements HomeContract.View, HomeContract.HomeDelegate {

    public static final String EXTRA_FLOW = "flow";
    public static final String EXTRA_AUTO_UPDATE = "auto_update";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    private static final String TAG = "HomeActivity";
    private DatabaseHelper databaseHelper;
    private ArrayList<User> userArrayList;
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private final int SUCCESS = 1, ERROR = 2;
    @Inject
    HomeContract.Presenter presenter;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.add_balance_tab)
    View addBalanceTabView;
    @BindView(R.id.cash_summary_tab)
    View cashSummaryTabView;
    @BindView(R.id.parent_tab)
    View parentTabView;
    @BindView(R.id.person_tab)
    View userListTabView;


    @BindView(R.id.change_password_tab)
    View changePasswordTabView;
    @BindView(R.id.notification_tab)
    View notificationTabView;
    @BindView(R.id.update_data_tab)
    View updateDataTabView;
    @BindView(R.id.notification_count_text)
    TextView notificationCountTextView;
    private ActionBarDrawerToggle toggle;
    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.onNotificationRefreshed();
        }
    };

    @OnClick({R.id.add_balance_tab, R.id.cash_summary_tab, R.id.parent_tab, R.id.person_tab,
            R.id.change_password_tab, R.id.notification_tab, R.id.update_data_tab})
    void onBottomNavigationItemClicked(View view) {
        switch (view.getId()) {
            case R.id.add_balance_tab:
                showAddBalanceScreen();
                selectMenuItemForFlow(Flow.ADD_BALANCE);
                break;
            case R.id.cash_summary_tab:
                showCashSummaryScreen();
                selectMenuItemForFlow(Flow.CASH_SUMMARY);
                break;
            case R.id.parent_tab:
                showParentUserScreen();
                break;
            case R.id.person_tab:
                showUserListScreen();
                break;
            case R.id.change_password_tab:
                showChangePasswordScreen();
                selectMenuItemForFlow(Flow.CHANGE_PASSWORD);
                break;
            case R.id.notification_tab:
                showNotificationScreen();
                selectMenuItemForFlow(Flow.NOTIFICATION);
                break;
            case R.id.update_data_tab:
                showUpdateScreen();
                selectMenuItemForFlow(Flow.UPDATE);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initDependency();
        initToolbar();
        initNavigationView();



        /**
         * @kns.p 1>coming Dashbord fragment to acLedger fragment
         * 2>coming Dashbord fragment to adduser fragment
         * 3>coming Dashbord fragment to purchase fragment
         * */
        Intent intent = getIntent();
        String from_last = intent.getStringExtra("from_last");

        if(intent.hasExtra("from_last")){

            if(intent.getStringExtra("from_last").equalsIgnoreCase(
                    "dashbordfrag_to_acledgerfrag")){
                toolbar.setTitle(getString(R.string.ac_ledger));
                showAcLedgerFragment();
            } else if(intent.getStringExtra("from_last").equalsIgnoreCase("dashbordfrag_to_purchasefrag")){
                toolbar.setTitle(getString(R.string.purchase_user));
                selectMenuItem(navigationView.getMenu().findItem(R.id.nav_purchase_user));
                showPurchaseUser();
            } else if(intent.getStringExtra("from_last").equalsIgnoreCase("dashbordfrag_to_adduserfrag")){
                toolbar.setTitle(getString(R.string.add_user));
                selectMenuItem(navigationView.getMenu().findItem(R.id.nav_add_user));
                showAddUser();
            }

        }

        else{
            //for existing flow of app....
            int flow = getIntent().getIntExtra(EXTRA_FLOW, Flow.ADD_BALANCE);
            presenter.initWithFlow(flow);
        }

    }

    @Override
    protected void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    private void initNavigationView() {

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                presenter.fetchBalance();
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return HomeActivity.this.onNavigationItemSelected(item);
            }
        });
    }

    private void selectMenuItem(MenuItem item) {
        if (item.getItemId() != R.id.nav_logout) {
            item.setChecked(true);
        }
    }

    public void selectMenuItemForFlow(@Flow int flow) {
        switch (flow) {
            case Flow.ADD_BALANCE:
                selectMenuItem(navigationView.getMenu().findItem(R.id.nav_recharge));
                break;
            case Flow.CASH_SUMMARY:
                selectMenuItem(navigationView.getMenu().findItem(R.id.nav_trans_search));
                break;
            case Flow.CHANGE_PASSWORD:
                selectMenuItem(navigationView.getMenu().findItem(R.id.nav_ChangePassword));
                break;
            case Flow.NOTIFICATION:
                selectMenuItem(navigationView.getMenu().findItem(R.id.nav_Notification));
                break;
            case Flow.UPDATE:
                selectMenuItem(navigationView.getMenu().findItem(R.id.nav_update_data));
                break;
        }
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        selectMenuItem(item);
        drawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.nav_Home:
                finish();
                break;
            case R.id.nav_recharge:
                toolbar.setTitle(getString(R.string.add_balance));
                showAddBalanceScreen();
                break;
            case R.id.nav_trans_search:
                toolbar.setTitle(getString(R.string.cash_summary));
                showCashSummaryScreen();
                break;

            case R.id.nav_ledger_view:
                toolbar.setTitle(getString(R.string.ac_ledger));
                showAcLedgerFragment();
                break;

            case R.id.nav_purchase_user:
                toolbar.setTitle(getString(R.string.purchase_user));
                showPurchaseUser();
                break;

            case R.id.nav_add_user:
                toolbar.setTitle(getString(R.string.add_user));
                showAddUser();
                break;
            case R.id.nav_update_data:
                showUpdateScreen();
                break;
            case R.id.nav_ChangePassword:
                showChangePasswordScreen();
                break;
            case R.id.nav_Notification:
                showNotificationScreen();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        return true;
    }

    @Override
    public void showLogoutConfirmationPopup() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_logout_message)
                .setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.onConfirmLogoutButtonClicked();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void logout() {
        presenter.onLogoutButtonClicked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (upIntent != null) {
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upIntent);
                finish();
            } else {
                onBackPressed();
            }
        }
    }

    private void closeNavDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private boolean isNavDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDependency() {
        DaggerHomeComponent.builder()
                .applicationComponent(((AppController) getApplication()).
                        getApplicationComponent())
                .homeModule(new HomeModule(this))
                .build()
                .inject(this);
    }

    public int getFragmentContainer() {
        return R.id.fragment_container;
    }

    @Override
    public void showAddBalanceScreen() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showAddBalanceScreen");
        replaceFragment(getFragmentContainer(), new AddBalanceFragment());
        updateBottomNavigation(Flow.ADD_BALANCE);
    }


    public void showAcLedgerFragment() {
        clearBackStack(getFragmentContainer());
        Log.e(TAG, "showAddBalanceScreen");
        replaceFragment(getFragmentContainer(), new AcLedgerFragment());
        updateBottomNavigation(Flow.ADD_BALANCE);
        toolbar.setTitle(getString(R.string.ac_ledger));
        setToolBarTitle(getResources().getString(R.string.ac_ledger));
    }

    public void setToolBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void showCashSummaryScreen() {
        clearBackStack(getFragmentContainer());
        replaceFragment(getFragmentContainer(), new CashSummaryFragment());
        updateBottomNavigation(Flow.CASH_SUMMARY);
    }

    @Override
    public void showCashBookScreen() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showCashBookScreen");
        updateBottomNavigation(Flow.CASHBOOK);
    }

    @Override
    public void showAddUser() {

        makeAddUserTypeApiCalls();

    }

    private void makeAddUserTypeApiCalls() {

        databaseHelper = new DatabaseHelper(HomeActivity.this);
        /* [START] - get user data from database and store into string variables */
        userArrayList = databaseHelper.getUserDetail();
        // Store user information in variables
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        strRegistrationDateTime = userArrayList.get(0).getReg_date();
        //user type thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.add_user_type;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    System.out.println("Add user type response1: " + response);
                    myHandler.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                parseSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                displayErrorDialog(msg.obj.toString());
            }
        }
    };

    // parse success response
    private void parseSuccessResponse(String response) {
        Log.e(TAG, " AccountLedger Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );

                Log.e(TAG,"AccountLedger : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = decryptAPI(encrypted_response);
                System.out.println("Add User Type: decrypted_response : " + decrypted_response);
                //Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);
                parsing_response(decrypted_response);
            } else if (jsonObject.getString("status").equals("2") &&
                    jsonObject.getString("message").equalsIgnoreCase("Invalid Details")) {

            } else {

            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(HomeActivity.this, "No result found");
            e.printStackTrace();
        }
    }

    public void parsing_response(String response) throws JSONException {

        JSONObject objectData = new JSONObject(response);
        JSONObject data = objectData.getJSONObject("usertype");
        if(data.length()>0) {
            loadAddUserFragment();

        } else {
            loadPurchaseUserFragment();
        }

    }

    public void loadPurchaseUserFragment() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showPurchaseUserScreen");
        replaceFragment(getFragmentContainer(), new PurchaseUserFragment());
        updateBottomNavigation(Flow.PURCHASE_USER);
        toolbar.setTitle(getString(R.string.purchase_user));
    }

    public void loadAddUserFragment() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showAddUserScreen");
        replaceFragment(getFragmentContainer(), new AddUserFragment());
        updateBottomNavigation(Flow.ADD_USER);
        toolbar.setTitle(getString(R.string.add_user));
    }



    /*Method : decryptAPI
       Decrypt response of webservice*/
    public String decryptAPI(String response) {
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();
        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, strMacAddress);
        String decrypted_response = null;
        byte[] decrypted_bytes = Base64.decode(response, Base64.DEFAULT);
        try {
            decrypted_response = new String(mCrypt.decrypt(mCrypt.bytesToHex(decrypted_bytes)), "UTF-8");
        }
        catch (Exception e) {
            Log.e(TAG,"Cashbook : " + "Error 7 : " + e.getMessage());
            e.printStackTrace();
        }
        return decrypted_response;
    }


    // display error in dialog
    private void displayErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        final android.app.AlertDialog alertDialog;
        try {
            alertDialog = new android.app.AlertDialog.Builder(HomeActivity.this).create();
            alertDialog.setTitle("Info!");
            alertDialog.setCancelable(false);
            alertDialog.setMessage(message);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        catch (Exception ex) {
            Log.e(TAG,"Error in error dialog");
            Log.e(TAG,"Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(HomeActivity.this, message);
            }
            catch (Exception e) {
                Log.e(TAG,"Error in toast message");
                Log.e(TAG,"ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    @Override
    public void showPurchaseUser() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showPurchaseUserScreen");
        replaceFragment(getFragmentContainer(), new PurchaseUserFragment());
        updateBottomNavigation(Flow.PURCHASE_USER);
        toolbar.setTitle(getString(R.string.purchase_user));
    }

    private void showParentUserScreen() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showParentUserScreen");
        replaceFragment(getFragmentContainer(), new ParentUserFragment());
        updateBottomNavigation(Flow.PARENT_USER);
    }

    @Override
    public void showChangePasswordScreen() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showChangePasswordScreen");
        replaceFragment(getFragmentContainer(), new ChangePasswordFragment());
        updateBottomNavigation(Flow.CHANGE_PASSWORD);
    }

    @Override
    public void showNotificationScreen() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showNotificationScreen");
        replaceFragment(getFragmentContainer(), NotificationFragment.getInstance(getIntent().getIntExtra(EXTRA_NOTIFICATION_ID, -1)));
        updateBottomNavigation(Flow.NOTIFICATION);
    }

    @Override
    public void showUpdateScreen() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showUpdateScreen");
        replaceFragment(
                getFragmentContainer(),
                UpdateDataFragment.getInstance(getIntent().getBooleanExtra(EXTRA_AUTO_UPDATE, false)));
        updateBottomNavigation(Flow.UPDATE);
    }

    @Override
    public void showUserListScreen() {
        clearBackStack(getFragmentContainer());
        Log.d(TAG, "showUserListScreen");
        replaceFragment(getFragmentContainer(), new UserListFragment());
        updateBottomNavigation(Flow.USER_LIST);

    }

    public void updateBottomNavigation(int flow) {
        hideAllBottomNavigationTabs();
        switch (flow) {
            case Flow.USER_LIST:
            case Flow.ADD_BALANCE:
                cashSummaryTabView.setVisibility(View.VISIBLE);
                parentTabView.setVisibility(View.VISIBLE);
                notificationTabView.setVisibility(View.VISIBLE);
                break;
            case Flow.CASH_SUMMARY:
                addBalanceTabView.setVisibility(View.VISIBLE);
                changePasswordTabView.setVisibility(View.VISIBLE);
                notificationTabView.setVisibility(View.VISIBLE);
                break;
            case Flow.CASHBOOK:
                break;
            case Flow.CHANGE_PASSWORD:
                addBalanceTabView.setVisibility(View.VISIBLE);
                cashSummaryTabView.setVisibility(View.VISIBLE);
                notificationTabView.setVisibility(View.VISIBLE);
                break;
            case Flow.NOTIFICATION:
                parentTabView.setVisibility(View.VISIBLE);
                userListTabView.setVisibility(View.VISIBLE);
                cashSummaryTabView.setVisibility(View.VISIBLE);
                break;
            case Flow.UPDATE:
                break;
            case Flow.PARENT_USER:
                cashSummaryTabView.setVisibility(View.VISIBLE);
                userListTabView.setVisibility(View.VISIBLE);
                notificationTabView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideAllBottomNavigationTabs() {
        addBalanceTabView.setVisibility(View.GONE);
        parentTabView.setVisibility(View.GONE);
        userListTabView.setVisibility(View.GONE);
        notificationTabView.setVisibility(View.GONE);
        updateDataTabView.setVisibility(View.GONE);
        changePasswordTabView.setVisibility(View.GONE);
        cashSummaryTabView.setVisibility(View.GONE);
    }

    @Override
    public void updateNavigationHeader(String userName, String name) {
        if (navigationView.getHeaderCount() > 0) {
            View header = navigationView.getHeaderView(0);
            TextView txtName = (TextView) header.findViewById(R.id.tv_header_name);
            TextView txtEmail = (TextView) header.findViewById(R.id.tv_header_email);
            txtName.setText(name);
            txtEmail.setText(userName);
        }
    }

    @Override
    public void registerNotificationReceiver(String action) {
        IntentFilter intentFilter = new IntentFilter(action);
        registerReceiver(mNotificationReceiver, intentFilter);
    }

    @Override
    public void unRegisterNotificationReceiver() {
        unregisterReceiver(mNotificationReceiver);
    }


    @Override
    public Context context() {
        return this;
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        super.showInfoDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void updateNotificationCount(int notificationCount) {
        View actionView = MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_Notification));
        TextView notificationCountTextView = (TextView) actionView.findViewById(R.id.txt_Item_NavigationMenu_Message);
        notificationCountTextView.setText(String.valueOf(notificationCount));
        HomeActivity.this.notificationCountTextView.setText(String.valueOf(notificationCount));
        updateNotificationBadgeVisibility(notificationCount > 0);

    }

    @Override
    public void showAddBalanceScreenForUser(ChildUserModel childUserModel) {
        clearBackStack(getFragmentContainer());
        AddBalanceFragment addBalanceFragment = new AddBalanceFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AddBalanceFragment.EXTRA_PHONE_NUMBER, childUserModel.getPhoneNo());
        bundle.putString(AddBalanceFragment.EXTRA_FIRM_NAME, childUserModel.getFirmName());
        addBalanceFragment.setArguments(bundle);
        replaceFragment(getFragmentContainer(), addBalanceFragment, true);
        hideAllBottomNavigationTabs();
    }

    @Override
    public void updateNotificationBadgeVisibility(boolean isVisible) {
        View actionView = MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_Notification));
        TextView notificationCountTextView = (TextView) actionView.findViewById(R.id.txt_Item_NavigationMenu_Message);
        notificationCountTextView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        HomeActivity.this.notificationCountTextView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showBalance(BigDecimal balance) {
        if (navigationView.getHeaderCount() > 0) {
            View header = navigationView.getHeaderView(0);
            TextView balanceTextView = (TextView) header.findViewById(R.id.tv_header_balance);
            balanceTextView.setText(getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(balance)));
        }
    }

    @Override
    public void onAddBalanceCompleted() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void showHomeScreen() {
        finish();
    }

    @Override
    public void disableDrawer() {
        int lockMode = DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void enableDrawer() {
        int lockMode = DrawerLayout.LOCK_MODE_UNLOCKED;
        drawerLayout.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(true);
    }
}
