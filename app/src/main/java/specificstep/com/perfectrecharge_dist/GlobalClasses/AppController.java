package specificstep.com.perfectrecharge_dist.GlobalClasses;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;
import specificstep.com.perfectrecharge_dist.di.components.DaggerApplicationComponent;
import specificstep.com.perfectrecharge_dist.di.modules.ApplicationModule;
import specificstep.com.perfectrecharge_dist.utility.NotificationUtil;

/**
 * Created by admin1 on 21/3/16.
 */

public class AppController extends Application {

    @Inject
    NotificationUtil notificationUtil;
    private ApplicationComponent applicationComponent;

    public NotificationUtil getNotificationUtil() {
        return notificationUtil;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*report crash if any issues with app */
        Fabric.with(this, new Crashlytics());

        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
