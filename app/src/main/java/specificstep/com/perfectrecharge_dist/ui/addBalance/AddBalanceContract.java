package specificstep.com.perfectrecharge_dist.ui.addBalance;

import android.content.Context;

import java.math.BigDecimal;
import java.util.List;

import specificstep.com.perfectrecharge_dist.Models.ChildUserModel;
import specificstep.com.perfectrecharge_dist.Models.UserList;
import specificstep.com.perfectrecharge_dist.ui.base.BasePresenter;

public interface AddBalanceContract {

    interface Presenter extends BasePresenter {

        void initialize();

        void onUserSelected(UserList userList);

        void refreshBalance();

        void onRechargeButtonClicked();

        void onConfirmRechargeButtonClicked();

        void onRechargeAmountChanged(CharSequence text);

        void loadUserDetailsByPhoneNumber(String email, String firmName);

        void onSearchUserTextChanged(CharSequence text);

        void onSelectedUser(ChildUserModel childUserModel);
    }

    interface View {

        void showBalance(BigDecimal value);

        Context context();

        void showInfoDialog(String errorMessage);

        void showUserName(String userName);

        void showFirmName(String firmName);

        void showEmail(String email);

        void showPhoneNumber(String phoneNo);

        void showAmount(float balance);

        void showTotalAmount(String balance);

        void showUserContainer();

        void hideAutoCompleteView();

        void showLoadingView();

        void hideLoadingView();

        CharSequence getUserEnteredAmount();

        void showToastMessage(String msg);

        String getRechargeBalance();

        void showErrorDialog(String msg);

        String getUserName();

        String getFirmName();

        String getPhone();

        String getEmail();

        String getCurrentBalance();

        String getTotalAmount();

        void showConfirmRechargePopup(
                String userName,
                String firmName,
                String phone,
                String email,
                String currentBalance,
                String rechargeBalance,
                String totalAmount);

        void goBack();

        void showAddBalanceSuccessPopup(String message);

        void setAutoCompleteText(String text);

        void showInvalidAccessTokenPopup();

        void setUserListAdapter(List<ChildUserModel> userModels);

        void hideUserListView();

        void showWarningDialog(String string);
    }
}
