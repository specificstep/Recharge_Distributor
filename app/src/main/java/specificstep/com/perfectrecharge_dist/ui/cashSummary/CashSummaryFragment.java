package specificstep.com.perfectrecharge_dist.ui.cashSummary;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import specificstep.com.perfectrecharge_dist.Adapters.CashSummeryAdapter;
import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.GlobalClasses.TransparentProgressDialog;
import specificstep.com.perfectrecharge_dist.Models.CashSummaryModel;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFragment;
import specificstep.com.perfectrecharge_dist.ui.home.HomeContract;

public class CashSummaryFragment extends BaseFragment implements CashSummaryContract.View, RadioGroup.OnCheckedChangeListener {

    @Inject
    CashSummaryContract.Presenter presenter;
    @BindView(R.id.ll_Search)
    LinearLayout searchContainer;
    @BindView(R.id.from_date)
    EditText fromDateEditText;
    @BindView(R.id.to_date)
    EditText toDateEditText;
    @BindView(R.id.dp_CashSummery_Result)
    DatePicker currentDatePicker;
    @BindView(R.id.radio)
    RadioGroup userTypeRadioGroup;
    @BindView(R.id.userList)
    Spinner userListSpinner;
    @BindView(R.id.ll_ResetSearch)
    LinearLayout resetControlsContainer;
    @BindView(R.id.txt_CashSummery_Result)
    TextView userTypeTextView;
    @BindView(R.id.ll_recycler_view)
    View searchResultContainerView;
    @BindView(R.id.lst_trans_search_fragment_trans_search)
    ListView searchResultListView;
    @BindView(R.id.rbt_Self)
    RadioButton selfRadioButton;
    @BindView(R.id.rbt_Reseller)
    RadioButton resellerRadioButton;
    DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            presenter.onFromDateSelected(year, month, dayOfMonth);
        }
    };
    DatePickerDialog.OnDateSetListener toDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            presenter.onToDateSelected(year, month, dayOfMonth);
        }
    };
    private boolean mIsInjected;
    private HomeContract.HomeDelegate homeDelegate;
    private TransparentProgressDialog transparentProgressDialog;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userTypeRadioGroup.setOnCheckedChangeListener(this);
        selfRadioButton.setChecked(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsInjected = injectDependency();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_summary, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_search_fragment_trans_search)
    void onSearchButtonClicked() {
        presenter.onSearchButtonClicked();
    }

    @OnClick(R.id.btn_ResetSearch)
    void onResetButtonClicked() {
        presenter.onResetButtonClicked();
    }

    @OnClick(R.id.from_date)
    void onFromDateEditTextClick() {
        presenter.onFromDateEditTextClicked();
    }

    @OnClick(R.id.to_date)
    void onToDateEditTextClick() {
        presenter.onToDateEditTextClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeContract.HomeDelegate) {
            homeDelegate = (HomeContract.HomeDelegate) context;
        }
    }

    @Override
    public void onDetach() {
        homeDelegate = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.cash_summary));
        }
        if (!mIsInjected) {
            mIsInjected = injectDependency();
        }
        presenter.initialize();
    }

    boolean injectDependency() {
        try {
            DaggerCashSummaryComponent.builder()
                    .applicationComponent(((AppController) getActivity().getApplication()).getApplicationComponent())
                    .cashSummaryModule(new CashSummaryModule(this))
                    .build()
                    .inject(this);
            return true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void hideResellerRadioButton() {
        resellerRadioButton.setVisibility(View.GONE);
    }

    @Override
    public void showResellerRadioButton() {
        resellerRadioButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateDatePicker(int year, int month, int dayOfMonth) {
        currentDatePicker.init(year, month, dayOfMonth, null);
    }

    @Override
    public void updateFromDate(String strDate) {
        fromDateEditText.setText(strDate);
    }

    @Override
    public void updateToDate(String strDate) {
        toDateEditText.setText(strDate);
    }

    @Override
    public void showDatePickerForFromDate(Calendar calendar) {
        long timeInMilliseconds = new Date().getTime();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                fromDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(timeInMilliseconds);
        datePickerDialog.show();
    }

    @Override
    public void showToDateDatePickerDialog(Calendar calendar, long maxTimeMillis) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                toDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxTimeMillis);
        datePickerDialog.show();
    }

    @Override
    public void hideUserListSpinner() {
        userListSpinner.setVisibility(View.GONE);
    }

    @Override
    public void showUserListSpinner() {
        userListSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void setUserSpinnerAdapter(List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner, data);
        userListSpinner.setAdapter(adapter);
    }

    @Override
    public void showSearchWidgetContainer() {
        searchContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideResetViewContainer() {
        resetControlsContainer.setVisibility(View.GONE);
    }

    @Override
    public void showToastMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public String getSelectedUserType() {
        return userListSpinner.getSelectedItem() != null ? userListSpinner.getSelectedItem().toString() : "";
    }

    @Override
    public boolean isResellerSelected() {
        return userTypeRadioGroup.getCheckedRadioButtonId() == R.id.rbt_Reseller;
    }

    @Override
    public boolean isRetailerSelected() {
        return userTypeRadioGroup.getCheckedRadioButtonId() == R.id.rbt_Retailer;
    }

    @Override
    public int getSelectedUserTypePosition() {
        return userListSpinner.getSelectedItemPosition();
    }

    @Override
    public void setListAdapter(List<CashSummaryModel> summaryModels) {
        CashSummeryAdapter cashSummeryAdapter_new = new CashSummeryAdapter(
                getActivity(),
                summaryModels,
                new CashSummeryAdapter.OnReverseBalanceListener() {
                    @Override
                    public void onBalanceReversed() {
                        presenter.reloadCashSummary();
                    }
                }
        );
        searchResultListView.setAdapter(cashSummeryAdapter_new);

    }

    @Override
    public void updateDetailText(String text) {
        userTypeTextView.setText(text);
    }

    @Override
    public void showResetViewContainer() {
        resetControlsContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchWidgetContainer() {
        searchContainer.setVisibility(View.GONE);
    }

    @Override
    public void showSearchResultContainer() {
        searchResultContainerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);

        if (!transparentProgressDialog.isShowing()) {
            transparentProgressDialog.show();
        }
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void hideProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
        switch (id) {
            case R.id.rbt_Reseller:
                presenter.onResellerUserRadioButtonChecked();
                break;
            case R.id.rbt_Retailer:
                presenter.onRetailerUserRadioButtonChecked();
                break;
            case R.id.rbt_Self:
                presenter.onSelfRadioButtonChecked();
                break;
        }
    }
}
