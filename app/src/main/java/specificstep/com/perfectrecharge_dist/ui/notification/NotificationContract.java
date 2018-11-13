package specificstep.com.perfectrecharge_dist.ui.notification;

import java.util.List;

import specificstep.com.perfectrecharge_dist.Models.NotificationModel;
import specificstep.com.perfectrecharge_dist.ui.base.BasePresenter;

public interface NotificationContract {

    interface Presenter extends BasePresenter {

        void initialize();

        void onNotificationRefreshed();

        void onNotificationItemClicked(NotificationModel notificationModel);

        void onNotificationPopupOkButtonClicked(NotificationModel notificationModel);

        void initializeWithId(int notificationId);
    }

    interface View {

        void registerNotificationBroadcastReceiver(String action);

        void unRegisterNotificationBroadcastReceiver();

        void showNotificationListView();

        void setNotificationAdapter(List<NotificationModel> value);

        void hideEmptyTextView();

        void hideNotificationListView();

        void showEmptyTextView();

        void hideLoadingView();

        void showLoadingView();

        void updateNotificationCount(Integer value);

        void showNotificationDetailDialog(NotificationModel notificationModel);

        void refreshNotificationListView();
    }
}
