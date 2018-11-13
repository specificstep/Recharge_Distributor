package specificstep.com.perfectrecharge_dist.ui.signup;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import specificstep.com.perfectrecharge_dist.GlobalClasses.TransparentProgressDialog;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFragment;
import specificstep.com.perfectrecharge_dist.ui.otpVerification.OtpVerificationActivity;
import specificstep.com.perfectrecharge_dist.utility.PermissionUtils;
import specificstep.com.perfectrecharge_dist.utility.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends BaseFragment implements SignUpContract.View {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;
    @BindView(R.id.edt_uname_or_email_act_reg)
    EditText userNameEditText;
    private SignUpContract.Presenter presenter;
    private TransparentProgressDialog transparentProgressDialog;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.btn_reg_app_act_reg)
    void onRegisterButtonClick() {
        presenter.register(userNameEditText.getText().toString().trim());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        userNameEditText.setFilters(new InputFilter[]{filter});
        readPhoneState();
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    public void readPhoneState() {
        if(PermissionUtils.hasPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)) return;
        if(PermissionUtils.shouldShowRequestPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            PermissionUtils.requestPermission(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void setProgressIndicator() {
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
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showError(String errorMsg) {
        Utility.toast(getActivity(), errorMsg);
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void showInternetNotAvailableDialog() {
        showDialog(getString(R.string.error), getString(R.string.message_no_intenet));
    }

    @Override
    public void hideProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void showVerifyRegistrationScreen(String userName) {
        Intent intent = new Intent(getActivity(), OtpVerificationActivity.class);
        intent.putExtra(OtpVerificationActivity.EXTRA_USERNAME, userName);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void setPresenter(SignUpContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
