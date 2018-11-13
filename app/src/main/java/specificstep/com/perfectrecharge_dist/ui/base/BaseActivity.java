package specificstep.com.perfectrecharge_dist.ui.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.GlobalClasses.Constants;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.data.source.local.Pref;
import specificstep.com.perfectrecharge_dist.ui.signIn.SignInActivity;
import specificstep.com.perfectrecharge_dist.ui.signup.SignUpActivity;

public class BaseActivity extends AppCompatActivity {

    @Inject
    Pref pref;

    private BroadcastReceiver mInvalidAccessTokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_INVALID_ACCESS_TOKEN.equals(intent.getAction())) {
                //Clear preferences and logout user
                clearUserDataAndLogout();
            }
        }
    };

    private void clearUserDataAndLogout() {
        pref.clearPref();
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Replace a {@link Fragment} to this activity's layout
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    public void replaceFragment(int containerViewId, Fragment fragment) {
        this.replaceFragment(containerViewId, fragment, false);
    }

    /**
     * Replace a {@link Fragment} to this activity's layout
     *
     * @param containerViewId The container view to where add the fragment
     * @param fragment        The fragment to be added
     * @param addToBackStack  boolean to add fragment to back stack.
     */
    protected void replaceFragment(int containerViewId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public void clearBackStack(int containerViewId) {
        getSupportFragmentManager().popBackStack(containerViewId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();
    }

    public void showInvalidAccessTokenPopup() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.invalid_access_token))
                .setCancelable(false)
                .setMessage(getString(R.string.invalid_token_message))
                .setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logoutUser();
                    }
                }).create().show();
    }

    public void logoutUser() {
        /*Intent intent = new Intent(Constants.ACTION_INVALID_ACCESS_TOKEN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerInValidTokenBroadcastReceiver();
    }

    private void registerInValidTokenBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        mInvalidAccessTokenReceiver,
                        new IntentFilter(Constants.ACTION_INVALID_ACCESS_TOKEN));
    }

    @Override
    protected void onDestroy() {
        unRegisterInValidTokenBroadcastReceiver();
        super.onDestroy();
    }

    private void unRegisterInValidTokenBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mInvalidAccessTokenReceiver);
    }
}
