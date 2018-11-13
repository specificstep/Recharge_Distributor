package specificstep.com.perfectrecharge_dist.ui.updateData;

import com.google.common.base.Strings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.Database.DatabaseHelper;
import specificstep.com.perfectrecharge_dist.GlobalClasses.Constants;
import specificstep.com.perfectrecharge_dist.Models.UserList;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.data.exceptions.InValidDetailsException;
import specificstep.com.perfectrecharge_dist.data.exceptions.InvalidAccessTokenException;
import specificstep.com.perfectrecharge_dist.data.source.local.Pref;
import specificstep.com.perfectrecharge_dist.interactors.DefaultObserver;
import specificstep.com.perfectrecharge_dist.interactors.usecases.GetChildUserUseCase;
import specificstep.com.perfectrecharge_dist.utility.NotificationUtil;

class UpdateDataPresenter implements UpdateDataContract.Presenter {

    private final UpdateDataContract.View view;
    private final Pref pref;
    private final DatabaseHelper databaseHelper;
    private final GetChildUserUseCase childUserUseCase;
    private final NotificationUtil notificationUtil;
    private boolean forceUpdate;

    @Inject
    UpdateDataPresenter(UpdateDataContract.View view, Pref pref, DatabaseHelper databaseHelper, GetChildUserUseCase childUserUseCase, NotificationUtil notificationUtil) {
        this.view = view;
        this.pref = pref;
        this.databaseHelper = databaseHelper;
        this.childUserUseCase = childUserUseCase;
        this.notificationUtil = notificationUtil;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        childUserUseCase.dispose();
    }

    @Override
    public void initialize(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
        updateLastUpdatedDate();
        if (forceUpdate) {
            onUpdateDataButtonClicked();
        }
    }

    private void updateLastUpdatedDate() {
        long lastUpdateMillis = pref.getValue(Pref.KEY_LAST_UPDATE_DATE, 0L);
        if (lastUpdateMillis != 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault());
            view.showLastUpdatedDate(simpleDateFormat.format(new Date(lastUpdateMillis)));
        } else {
            view.hideLastUpdatedDateView();
        }
    }

    @Override
    public void onUpdateDataButtonClicked() {
        view.hideUpdateDataButton();
        view.showProgressBar();
        view.showStatusBar();
        view.hideLastUpdateDateView();
        view.updateProgress(0);
        databaseHelper.deleteUserListDetail();
        view.disableDrawer();
        fetchUpdate();
    }

    private void fetchUpdate() {
        view.updateStatusText(view.context().getString(R.string.status_updating));
        childUserUseCase.execute(new DefaultObserver<List<UserList>>() {
            @Override
            public void onNext(List<UserList> value) {
                super.onNext(value);
                onGetChildSuccess(value);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if(e instanceof InvalidAccessTokenException) {
                    view.showInvalidAccessTokenPopup();
                    return;
                }
                onGetChildError(e);
            }
        }, null);
    }

    private void onGetChildError(Throwable e) {
        String message;
        if (Strings.isNullOrEmpty(e.getMessage())) {
            message = "";
        } else {
            message = e.getMessage().toLowerCase().trim();
        }

        updateLastUpdatedDate();
        view.showHomeButton();
        view.updateStatusText(view.context().getString(R.string.status_update_failed_format, message));

        view.showErrorDialog(e.getMessage());
        view.showUpdateDataButton();
        view.hideProgressBar();
    }

    private void onGetChildSuccess(List<UserList> userLists) {
        view.updateProgress(100);
        notificationUtil.sendNotification(view.context().getString(R.string.update), view.context().getString(R.string.data_update_success));
        pref.setValue(Pref.KEY_LAST_UPDATE_DATE, System.currentTimeMillis());
        updateLastUpdatedDate();
        view.updateStatusText(view.context().getString(R.string.status_update_completed));
        view.showHomeButton();
        if (forceUpdate) {
            view.goBack();
        }
        view.enableDrawer();
    }

    @Override
    public void onHomeButtonClicked() {
        view.goBack();
    }

    @Override
    public void onLogoutButtonClicked() {
        pref.clearPref();
        view.showSignInScreen();
    }
}
