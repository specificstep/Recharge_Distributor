package specificstep.com.perfectrecharge_dist.ui.addBalance;

import android.text.TextUtils;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import specificstep.com.perfectrecharge_dist.Database.ChildUserTable;
import specificstep.com.perfectrecharge_dist.Database.DatabaseHelper;
import specificstep.com.perfectrecharge_dist.GlobalClasses.Constants;
import specificstep.com.perfectrecharge_dist.Models.ChildUserModel;
import specificstep.com.perfectrecharge_dist.Models.UserList;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.data.exceptions.InvalidAccessTokenException;
import specificstep.com.perfectrecharge_dist.data.source.local.Pref;
import specificstep.com.perfectrecharge_dist.exceptions.ErrorMessageFactory;
import specificstep.com.perfectrecharge_dist.interactors.DefaultObserver;
import specificstep.com.perfectrecharge_dist.interactors.exception.DefaultErrorBundle;
import specificstep.com.perfectrecharge_dist.interactors.usecases.AddBalanceUseCase;
import specificstep.com.perfectrecharge_dist.interactors.usecases.GetBalanceUseCase;
import specificstep.com.perfectrecharge_dist.interactors.usecases.GetChildUserUseCase;
import specificstep.com.perfectrecharge_dist.interactors.usecases.GetUserUseCase;
import specificstep.com.perfectrecharge_dist.utility.NotificationUtil;
import specificstep.com.perfectrecharge_dist.utility.Utility;

class AddBalancePresenter implements AddBalanceContract.Presenter {

    private static final String TAG = AddBalancePresenter.class.getSimpleName();
    private final NotificationUtil notificationUtils;
    private final ChildUserTable childUserTable;
    private final Pref pref;
    private AddBalanceContract.View view;
    private DatabaseHelper databaseHelper;
    private GetBalanceUseCase getBalanceUseCase;
    private GetUserUseCase getUserUseCase;
    private final GetChildUserUseCase childUserUseCase;
    private AddBalanceUseCase addBalanceUseCase;
    private BigDecimal currentBalance;
    private ChildUserModel mChildUserModel;
    private CharSequence searchTerm;

    @Inject
    AddBalancePresenter(AddBalanceContract.View view,
                        DatabaseHelper databaseHelper,
                        GetBalanceUseCase getBalanceUseCase,
                        GetUserUseCase getUserUseCase,
                        AddBalanceUseCase addBalanceUseCase,
                        NotificationUtil notificationUtils,
                        ChildUserTable childUserTable,
                        Pref pref, GetChildUserUseCase childUserUseCase) {
        this.view = view;
        this.databaseHelper = databaseHelper;
        this.getBalanceUseCase = getBalanceUseCase;
        this.getUserUseCase = getUserUseCase;
        this.addBalanceUseCase = addBalanceUseCase;
        this.notificationUtils = notificationUtils;
        this.childUserTable = childUserTable;
        this.pref = pref;
        this.childUserUseCase = childUserUseCase;
    }

    @Override
    public void start() {
        fetchBalance(false);
    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        addBalanceUseCase.dispose();
        getBalanceUseCase.dispose();
        getUserUseCase.dispose();
        childUserUseCase.dispose();
    }

    @Override
    public void initialize() {
        fetchAndShowUsersFromLocalStorage(null);
    }

    @Override
    public void loadUserDetailsByPhoneNumber(String phoneNumber, String firmName) {
        view.setAutoCompleteText(firmName + " - " + phoneNumber);
        UserList userList = databaseHelper.getUserListDetailsByPhoneNumber(phoneNumber);
        onUserSelected(userList);
    }

    @Override
    public void onSearchUserTextChanged(CharSequence text) {
        this.searchTerm = text;
        //Filter data
        fetchAndShowUsersFromLocalStorage(text);
    }

    private void fetchUsersFromServer() {
        childUserUseCase.execute(new DefaultObserver<List<UserList>>() {
            @Override
            public void onNext(List<UserList> value) {
                super.onNext(value);
                view.hideLoadingView();
                fetchAndShowUsersFromLocalStorage(searchTerm);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideLoadingView();
                if(e instanceof InvalidAccessTokenException) {
                    view.showInvalidAccessTokenPopup();
                    return;
                }
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

        }, null);
    }

    private void fetchAndShowUsersFromLocalStorage(final CharSequence searchTerm) {
        Observable.fromCallable(new Callable<List<ChildUserModel>>() {
            @Override
            public List<ChildUserModel> call() throws Exception {
                return getUsersFromLocalStorage(searchTerm);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DefaultObserver<List<ChildUserModel>>() {
                    @Override
                    public void onNext(List<ChildUserModel> value) {
                        super.onNext(value);
                        onChildUserReceived(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private void onChildUserReceived(List<ChildUserModel> value) {
        if (value != null && value.size() > 0) {
            view.setUserListAdapter(value);
        }
    }

    @Override
    public void onSelectedUser(ChildUserModel childUserModel) {
        fetchUserDetails(childUserModel.getEmail());
    }

    private List<ChildUserModel> getUsersFromLocalStorage(CharSequence searchTerm) {
        if (!TextUtils.isEmpty(searchTerm)) {
            String whereClause = "upper(" + ChildUserTable.KEY_FIRMNAME + ") like \'" + searchTerm.toString().toUpperCase() + "%\'" +
                    " OR upper(" + ChildUserTable.KEY_EMAIL + ") like \'" + searchTerm.toString().toUpperCase() + "%\'" +
                    " OR upper(" + ChildUserTable.KEY_USERNAME + ") like \'" + searchTerm.toString().toUpperCase() + "%\'" +
                    " OR upper(" + ChildUserTable.KEY_PHONENO + ") like \'" + searchTerm.toString().toUpperCase() + "%\'";
            return childUserTable.select_Data(whereClause);
        } else {
            return childUserTable.select_Data();
        }
    }

    @Override
    public void onUserSelected(UserList userList) {
        fetchUserDetails(userList.getEmail());
    }

    private void fetchUserDetails(String email) {
        view.showLoadingView();
        getUserUseCase.execute(new DefaultObserver<ChildUserModel>() {
            @Override
            public void onNext(ChildUserModel value) {
                super.onNext(value);
                onGetUserSuccess(value);
                view.hideLoadingView();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideLoadingView();
                if (e instanceof InvalidAccessTokenException) {
                    view.showInvalidAccessTokenPopup();
                    return;
                }
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetUserUseCase.Params.toParams(email));
    }

    private void onGetUserSuccess(ChildUserModel userModel) {
        this.mChildUserModel = userModel;
        view.showUserName(userModel.getUserName());
        view.showFirmName(userModel.getFirmName());
        view.showEmail(userModel.getEmail());
        view.showPhoneNumber(userModel.getPhoneNo());
        view.showAmount(userModel.getBalance());
        view.showTotalAmount(Utility.formatBigDecimalToString(new BigDecimal(userModel.getBalance())));
        view.showUserContainer();
        view.hideAutoCompleteView();
        view.hideUserListView();
    }

    @Override
    public void refreshBalance() {
        fetchBalance(false);
    }

    private void fetchBalance(final boolean proceedNext) {
        String cachedBalance = pref.getValue(Pref.KEY_BALANCE, "");
        if(!TextUtils.isEmpty(cachedBalance)) {
            AddBalancePresenter.this.currentBalance = new BigDecimal(cachedBalance);
        }

        if(AddBalancePresenter.this.currentBalance != null) {
            view.showBalance(AddBalancePresenter.this.currentBalance);
        }
        getBalanceUseCase.execute(new DefaultObserver<BigDecimal>() {
            @Override
            public void onNext(BigDecimal value) {
                super.onNext(value);
                AddBalancePresenter.this.currentBalance = value;
                view.showBalance(value);
                if (proceedNext) {
                    onRechargeButtonClicked();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e instanceof InvalidAccessTokenException) {
                    view.showInvalidAccessTokenPopup();
                    return;
                }
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, null);
    }


    @Override
    public void onRechargeButtonClicked() {
        if (checkValidations()) {
            view.showConfirmRechargePopup(
                    view.getUserName(),
                    view.getFirmName(),
                    view.getPhone(),
                    view.getEmail(),
                    view.getCurrentBalance(),
                    view.getRechargeBalance(),
                    view.getTotalAmount());
        }
    }

    @Override
    public void onConfirmRechargeButtonClicked() {
        if (Strings.isNullOrEmpty(mChildUserModel.getId()) || "0".equals(mChildUserModel.getId())) {
            view.goBack();
            return;
        }
        proceedToRecharge();
    }

    @Override
    public void onRechargeAmountChanged(CharSequence text) {
        try {
            BigDecimal totalBigDecimal = new BigDecimal(view.getCurrentBalance()).add(new BigDecimal(text.toString()));
            view.showTotalAmount(String.valueOf(totalBigDecimal.floatValue()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void proceedToRecharge() {
        view.showLoadingView();
        addBalanceUseCase.execute(
                new DefaultObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        super.onNext(value);
                        view.hideLoadingView();
                        onAddBalanceSuccess(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.hideLoadingView();
                        if (e instanceof InvalidAccessTokenException) {
                            view.showInvalidAccessTokenPopup();
                            return;
                        }
                        showErrorMessage(new DefaultErrorBundle((Exception) e));
                    }
                },
                AddBalanceUseCase.Params.toParams(
                        mChildUserModel.getId(),
                        view.getRechargeBalance()));
    }

    private void onAddBalanceSuccess(String message) {
        fetchBalance(false);
        fetchUsersFromServer();
        String userName = view.getUserName();
        String phone = view.getPhone();
        String totalAmount = view.getTotalAmount();
        String rechargeBalance = view.getRechargeBalance();
        String notificationMessage = message + "\n" +
                view.context().getString(R.string.user_name_format, userName) + "\n" +
                view.context().getString(R.string.mobile_number_format, phone) + "\n" +
                view.context().getString(R.string.balance_format_format, view.context().getString(R.string.currency_format, totalAmount));

        notificationUtils.sendNotification(view.context().getString(R.string.add_balance), notificationMessage);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault());

        String popupMessage = message + "\n" +
//                view.context().getString(R.string.user_name_format, userName) + "\n" +
                view.context().getString(R.string.mobile_number_format, phone) + "\n" +
                view.context().getString(
                        R.string.amount_format,
                        view.context().getString(
                                R.string.currency_format,
                                rechargeBalance)) + "\n" +
                view.context().getString(R.string.remaining_balance_format_format,
                        view.context().getString(
                                R.string.currency_format,
                                currentBalance.subtract(new BigDecimal(rechargeBalance)).toString())) + "\n" +
                view.context().getString(R.string.date_time_format, simpleDateFormat.format(new Date()));

        view.showAddBalanceSuccessPopup(popupMessage);

    }

    private boolean checkValidations() {
        if (TextUtils.isEmpty(view.getUserEnteredAmount())) {
            view.showToastMessage(view.context().getString(R.string.enter_amount));
            return false;
        } else if (isBalanceLargerThanCurrentBalance()) {
            view.showWarningDialog(view.context().getString(R.string.error_message_add_balance_more_than_current));
            return false;
        } else if (currentBalance.floatValue() <= 0) {
            view.showWarningDialog(view.context().getString(R.string.enter_valid_amount));
            return false;
        } else if(new BigDecimal(view.getUserEnteredAmount().toString()).floatValue() == 0) {
            view.showWarningDialog(view.context().getString(R.string.enter_valid_amount));
            return false;
        }
        return true;
    }

    private boolean isBalanceLargerThanCurrentBalance() {
        return new BigDecimal(view.getRechargeBalance()).compareTo(currentBalance) > 0;
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showInfoDialog(errorMessage);
    }
}
