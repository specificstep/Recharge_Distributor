package specificstep.com.perfectrecharge_dist.ui.updateData;

import android.content.Context;

import specificstep.com.perfectrecharge_dist.ui.base.BasePresenter;

public interface UpdateDataContract {

    interface Presenter extends BasePresenter {

        void initialize(boolean forceUpdate);

        void onUpdateDataButtonClicked();

        void onHomeButtonClicked();

        void onLogoutButtonClicked();
    }

    interface View {

        void goBack();

        void showLastUpdatedDate(String strDate);

        void hideLastUpdatedDateView();

        void hideUpdateDataButton();

        void showProgressBar();

        void showStatusBar();

        void hideLastUpdateDateView();

        void updateProgress(int progress);

        void disableDrawer();

        Context context();

        void updateStatusText(String status);

        void showHomeButton();

        void enableDrawer();

        void showSignInScreen();

        void showErrorDialog(String errorMessage);

        void showUpdateDataButton();

        void hideProgressBar();

        void showInvalidAccessTokenPopup();
    }
}
