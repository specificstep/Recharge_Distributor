package specificstep.com.perfectrecharge_dist.di.components;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.Database.ChildUserTable;
import specificstep.com.perfectrecharge_dist.Database.NotificationTable;
import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.GlobalClasses.EncryptionUtil;
import specificstep.com.perfectrecharge_dist.data.source.local.Pref;
import specificstep.com.perfectrecharge_dist.di.modules.ApplicationModule;
import specificstep.com.perfectrecharge_dist.di.modules.SharedPreferencesModule;
import specificstep.com.perfectrecharge_dist.interactors.executor.PostExecutionThread;
import specificstep.com.perfectrecharge_dist.interactors.executor.ThreadExecutor;
import specificstep.com.perfectrecharge_dist.interactors.repositories.UserRepository;
import specificstep.com.perfectrecharge_dist.utility.NotificationUtil;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = {
        ApplicationModule.class,
        SharedPreferencesModule.class
})
public interface ApplicationComponent {

    //Exposed to sub-graphs.
    Context context();
    ThreadExecutor threadExecutor();
    PostExecutionThread postExecutionThread();
    UserRepository userRepository();
    Pref pref();
    NotificationTable notificationTable();
    EncryptionUtil encryptionUtils();
    ChildUserTable childUserTable();
    NotificationUtil notificationUtil();

    void inject(AppController appController);
}
