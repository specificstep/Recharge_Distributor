package specificstep.com.perfectrecharge_dist.ui.parentUser;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import specificstep.com.perfectrecharge_dist.Adapters.ParentUserAdapter;
import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.GlobalClasses.TransparentProgressDialog;
import specificstep.com.perfectrecharge_dist.Models.ParentUserModel;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFragment;
import specificstep.com.perfectrecharge_dist.ui.home.HomeContract;

public class ParentUserFragment extends BaseFragment implements ParentUserContract.View {

    @Inject
    ParentUserContract.Presenter presenter;
    @BindView(R.id.txt_ParentUser_FirmName)
    TextView firmNameTextView;
    @BindView(R.id.txt_ParentUser_Name)
    TextView parentUserNameTextView;
    @BindView(R.id.txt_ParentUser_MobileNo)
    TextView phoneTextView;
    @BindView(R.id.txt_ParentUser_UserType)
    TextView userTypeTextView;
    @BindView(R.id.ll_ParentUser)
    View listContainer;
    @BindView(R.id.lst_ParentUser_ParentUserDetails)
    ListView userListView;
    @BindView(R.id.txt_ParentUser_NoMoreData)
    TextView noDataTextView;
    private boolean mIsInjected;
    private HomeContract.HomeDelegate homeDelegate;
    private TransparentProgressDialog transparentProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsInjected = injectDependency();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_user, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.parent_user));
        }
        if (!mIsInjected) {
            mIsInjected = injectDependency();
        }
        presenter.initialize();
    }

    boolean injectDependency() {
        try {
            DaggerParentUserComponent.builder()
                    .applicationComponent(((AppController) getActivity().getApplication()).getApplicationComponent())
                    .parentUserModule(new ParentUserModule(this))
                    .build()
                    .inject(this);
            return true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
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
    public void showLoadingView() {
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
    public void hideLoadingView() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void showInfoDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void setUpAdapter(List<ParentUserModel> parentUsers) {
        ParentUserAdapter adapter = new ParentUserAdapter(getActivity(), parentUsers);
        userListView.setAdapter(adapter);
    }

    @Override
    public void setFirmName(String firmName) {
        firmNameTextView.setText(firmName);
    }

    @Override
    public void setName(String name) {
        parentUserNameTextView.setText(name);
    }

    @Override
    public void setMobileNumber(String mobile) {
        phoneTextView.setText(mobile);
    }

    @Override
    public void setUserType(String userType) {
        userTypeTextView.setText(userType);
    }

    @Override
    public void showListContainer() {
        listContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideListContainer() {
        listContainer.setVisibility(View.GONE);
    }

    @Override
    public void hideNoDataLabel() {
        noDataTextView.setVisibility(View.GONE);
    }

    @Override
    public void showNoDataLabel() {
        noDataTextView.setVisibility(View.VISIBLE);
    }
}
