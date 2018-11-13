package specificstep.com.perfectrecharge_dist.ui.splash;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFragment;
import specificstep.com.perfectrecharge_dist.ui.dashboard.DashboardActivity;
import specificstep.com.perfectrecharge_dist.ui.signIn.SignInActivity;
import specificstep.com.perfectrecharge_dist.ui.signup.SignUpActivity;

public class SplashFragment extends BaseFragment implements SplashContract.View {

    private SplashContract.Presenter presenter;
    private Handler handler;

    @Override
    public void setPresenter(SplashContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();
        presenter.initialize();
    }

    @Override
    public void scheduleTimeout(long milliSeconds) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.onTimeoutCompleted();
            }
        }, milliSeconds);
    }

    @Override
    public void startMainScreen() {
        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void startLoginScreen() {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void startSignUpScreen() {
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void createFirstTimeNotification() {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText("App installed successfully and shortcut created.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        }
        // Dismiss notification after action has been clicked
        mBuilder.setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getActivity(), SplashActivity.class);
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SplashActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void showErrorDialog(@StringRes int strResId) {
        showDialog(getString(R.string.error), getString(strResId), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });
    }
}
