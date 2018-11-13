package specificstep.com.perfectrecharge_dist.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.data.executor.JobExecutor;
import specificstep.com.perfectrecharge_dist.data.net.RestApi;
import specificstep.com.perfectrecharge_dist.data.net.RestApiImpl;
import specificstep.com.perfectrecharge_dist.data.net.retrofit.RechargeService;
import specificstep.com.perfectrecharge_dist.data.net.retrofit.RetrofitClient;
import specificstep.com.perfectrecharge_dist.data.source.UserDataRepository;
import specificstep.com.perfectrecharge_dist.data.source.UserDataStore;
import specificstep.com.perfectrecharge_dist.data.source.remote.CloudUserDataStore;
import specificstep.com.perfectrecharge_dist.interactors.executor.PostExecutionThread;
import specificstep.com.perfectrecharge_dist.interactors.executor.ThreadExecutor;
import specificstep.com.perfectrecharge_dist.interactors.repositories.UserRepository;
import specificstep.com.perfectrecharge_dist.ui.UIThread;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {
    private final AppController application;

    public ApplicationModule(AppController application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    UserRepository provideUserRepository(UserDataRepository userDataRepository) {
        return userDataRepository;
    }

    @Provides
    @Singleton
    UserDataStore provideUserDataStore(CloudUserDataStore cloudUserDataStore) {
        return cloudUserDataStore;
    }

    @Provides
    @Singleton
    RechargeService provideRechargeService(RetrofitClient retrofitClient) {
        return retrofitClient.createService();
    }

    @Provides
    @Singleton
    RestApi provideRestApi(RestApiImpl restApi) {
        return restApi;
    }

    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }
}
