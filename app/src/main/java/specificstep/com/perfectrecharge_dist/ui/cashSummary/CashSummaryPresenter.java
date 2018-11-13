package specificstep.com.perfectrecharge_dist.ui.cashSummary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import specificstep.com.perfectrecharge_dist.Database.DatabaseHelper;
import specificstep.com.perfectrecharge_dist.GlobalClasses.Constants;
import specificstep.com.perfectrecharge_dist.Models.CashSummaryModel;
import specificstep.com.perfectrecharge_dist.Models.UserList;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.data.exceptions.InvalidAccessTokenException;
import specificstep.com.perfectrecharge_dist.data.source.local.Pref;
import specificstep.com.perfectrecharge_dist.data.utils.UserType;
import specificstep.com.perfectrecharge_dist.exceptions.ErrorMessageFactory;
import specificstep.com.perfectrecharge_dist.interactors.DefaultObserver;
import specificstep.com.perfectrecharge_dist.interactors.exception.DefaultErrorBundle;
import specificstep.com.perfectrecharge_dist.interactors.usecases.GetCashSummaryUseCase;

public class CashSummaryPresenter implements CashSummaryContract.Presenter {

    private final CashSummaryContract.View view;
    private final Pref pref;
    private final DatabaseHelper databaseHelper;
    private final SimpleDateFormat simpleDateFormat;
    private final GetCashSummaryUseCase cashSummaryUseCase;
    private Calendar fromDateCalendar, toDateCalendar;
    private ArrayList<UserList> userListArrayList;
    private StringBuilder stringBuilder = new StringBuilder();

    @Inject
    CashSummaryPresenter(
            CashSummaryContract.View view,
            Pref pref,
            DatabaseHelper databaseHelper,
            GetCashSummaryUseCase cashSummaryUseCase) {
        this.view = view;
        this.pref = pref;
        this.databaseHelper = databaseHelper;
        this.cashSummaryUseCase = cashSummaryUseCase;
        simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        fromDateCalendar = Calendar.getInstance();
        toDateCalendar = Calendar.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        cashSummaryUseCase.dispose();
    }

    @Override
    public void initialize() {
        updateResellerRadioButtonVisibility();
        updateCurrentDate();
        updateFromDate();
        updateToDate();
    }

    private void updateToDate() {
        view.updateToDate(simpleDateFormat.format(toDateCalendar.getTime()));
    }

    private void updateFromDate() {
        view.updateFromDate(simpleDateFormat.format(fromDateCalendar.getTime()));
    }

    private void updateCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        view.updateDatePicker(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void updateResellerRadioButtonVisibility() {
        if (pref.getValue(Pref.KEY_USER_TYPE, 0) == UserType.RESELLER.getType()) {
            view.hideResellerRadioButton();
        } else {
            view.showResellerRadioButton();
        }
    }

    @Override
    public void onSearchButtonClicked() {
        if (checkValidations()) {
            getCashSummary();
        }
    }

    private void getCashSummary() {
        SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fromDate = serverDateFormat.format(fromDateCalendar.getTime());
        String toDate = serverDateFormat.format(toDateCalendar.getTime());
        stringBuilder = new StringBuilder();
        stringBuilder.append(view.context().getString(R.string.from_label) + " " + simpleDateFormat.format(fromDateCalendar.getTime()) + "   |   ");
        stringBuilder.append(view.context().getString(R.string.to_label) + " " + simpleDateFormat.format(toDateCalendar.getTime()) + "\n");

        String selectedUserType = view.getSelectedUserType();
        int userType;
        boolean isAllSelected = view.context().getString(R.string.all).equals(selectedUserType);
        if (view.isResellerSelected()) {
            stringBuilder.append(view.context().getString(R.string.user_type_format, view.context().getString(R.string.reseller)) + "\n");
            if (isAllSelected) {
                stringBuilder.append(view.context().getString(R.string.user_all));
            }
            userType = UserType.RESELLER.getType();
        } else if (view.isRetailerSelected()) {
            stringBuilder.append(view.context().getString(R.string.user_type_format, view.context().getString(R.string.retailer)) + "\n");
            if (isAllSelected) {
                stringBuilder.append(view.context().getString(R.string.user_all));
            }
            userType = UserType.RETAILER.getType();
        } else {
            stringBuilder.append(view.context().getString(R.string.user_type_format, view.context().getString(R.string.self)) + "\n");
            if (isAllSelected) {
                stringBuilder.append(view.context().getString(R.string.user_self));
            }
            userType = UserType.SELF.getType();
        }
        int selectedUserTypePosition = view.getSelectedUserTypePosition();
//        if(userListArrayList.size() <= selectedUserTypePosition) {
//            return;
//        }
        String selectedUserId;
        if (isAllSelected) {
            selectedUserId = selectedUserType;
        } else {

            UserList userList = userListArrayList.get(selectedUserTypePosition - 1);
            selectedUserId = userList.getUser_id();
        }
        stringBuilder.append(view.context().getString(R.string.user_format, selectedUserType));

        view.showProgressIndicator();

        cashSummaryUseCase.execute(
                new DefaultObserver<List<CashSummaryModel>>() {
                    @Override
                    public void onNext(List<CashSummaryModel> value) {
                        super.onNext(value);
                        onCashSummaryReceived(value);
                        view.hideProgressIndicator();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.hideProgressIndicator();
                        if (e instanceof InvalidAccessTokenException) {
                            view.showInvalidAccessTokenPopup();
                            return;
                        }
                        showErrorMessage(new DefaultErrorBundle((Exception) e));
                    }
                },
                GetCashSummaryUseCase.Params.toParams(selectedUserId, userType, fromDate, toDate));
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }

    private void onCashSummaryReceived(List<CashSummaryModel> summaryModels) {
        view.showSearchResultContainer();
        view.setListAdapter(summaryModels);
        view.updateDetailText(stringBuilder.toString());
        view.showResetViewContainer();
        view.hideSearchWidgetContainer();
    }

    private boolean checkValidations() {
        if (fromDateCalendar.get(Calendar.MONTH) != toDateCalendar.get(Calendar.MONTH)) {
            view.showToastMessage(view.context().getString(R.string.message_select_same_months));
            return false;
        }
        return true;
    }

    @Override
    public void onResetButtonClicked() {
        view.showSearchWidgetContainer();
        view.hideResetViewContainer();
    }

    @Override
    public void onFromDateEditTextClicked() {
        view.showDatePickerForFromDate(fromDateCalendar);
    }

    @Override
    public void onToDateEditTextClicked() {
        Calendar currentCalendar = Calendar.getInstance();
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);

        currentCalendar.set(Calendar.MONTH, fromDateCalendar.get(Calendar.MONTH));

        int monthOfYear = fromDateCalendar.get(Calendar.MONTH);
        int lastDayOfMonth = fromDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (monthOfYear == currentMonth) {
            if (currentDayOfMonth < lastDayOfMonth) {
                // if current date is less then last day of month then set current date in to calender
                currentCalendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth);
            } else {
                // else set last day of month in to calender
                currentCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
            }
        } else {
            currentCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
        }

        long maxTimeMillis = currentCalendar.getTimeInMillis();
        view.showToDateDatePickerDialog(toDateCalendar, maxTimeMillis);

    }

    @Override
    public void onFromDateSelected(int year, int month, int dayOfMonth) {
        fromDateCalendar.set(year, month, dayOfMonth);
        updateFromDate();
        toDateCalendar.set(Calendar.YEAR, year);
        toDateCalendar.set(Calendar.MONTH, month);

        int lastDayOfMonth = fromDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);


        Calendar currentCalendar = Calendar.getInstance();
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
        if (currentMonth == month) {
            if (currentDayOfMonth < lastDayOfMonth) {
                // if current date is less then last day of month then set current date in to calender
                toDateCalendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth);
            } else {
                // else set last day of month in to calender
                toDateCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
            }
        } else {
            toDateCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
        }
        updateToDate();
    }

    @Override
    public void onToDateSelected(int year, int month, int dayOfMonth) {
        toDateCalendar.set(year, month, dayOfMonth);
        updateToDate();
    }

    @Override
    public void onResellerUserRadioButtonChecked() {
        List<UserList> userListArrayList = databaseHelper.getUserListDetailsByType("Reseller");
        view.showUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    @Override
    public void onRetailerUserRadioButtonChecked() {
        userListArrayList = databaseHelper.getUserListDetailsByType("Retailer");
        view.showUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    private void prepareUserListSpinnerData(List<UserList> userListArrayList) {
        List<String> data = new ArrayList<>();
        data.add(view.context().getString(R.string.all));
        for (UserList userList : userListArrayList) {
            data.add(userList.getUser_name() + " - " + userList.getPhone_no());
        }
        view.setUserSpinnerAdapter(data);
    }

    @Override
    public void onSelfRadioButtonChecked() {
        userListArrayList = databaseHelper.getUserListDetailsById();
        view.hideUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    @Override
    public void reloadCashSummary() {
        getCashSummary();
    }
}
