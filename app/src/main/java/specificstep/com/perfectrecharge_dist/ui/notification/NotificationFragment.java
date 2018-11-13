package specificstep.com.perfectrecharge_dist.ui.notification;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import specificstep.com.perfectrecharge_dist.Adapters.NotificationAdapter;
import specificstep.com.perfectrecharge_dist.GlobalClasses.AppController;
import specificstep.com.perfectrecharge_dist.GlobalClasses.TransparentProgressDialog;
import specificstep.com.perfectrecharge_dist.Models.NotificationModel;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.ui.base.BaseFragment;
import specificstep.com.perfectrecharge_dist.ui.home.HomeContract;
import specificstep.com.perfectrecharge_dist.utility.DateTime;

import static specificstep.com.perfectrecharge_dist.ui.home.HomeActivity.EXTRA_NOTIFICATION_ID;

public class NotificationFragment extends BaseFragment implements NotificationContract.View {

    @Inject
    NotificationContract.Presenter presenter;
    @BindView(R.id.txt_NewNotification)
    TextView emptyTextView;
    @BindView(R.id.notification_list_view)
    ListView notificationListView;
    private boolean mIsInjected;
    private HomeContract.HomeDelegate homeDelegate;

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.onNotificationRefreshed();
        }
    };
    private TransparentProgressDialog transparentProgressDialog;

    public static NotificationFragment getInstance(int notificationId) {
        NotificationFragment fragment = new NotificationFragment();
        if (notificationId != -1) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(EXTRA_NOTIFICATION_ID, notificationId);
            fragment.setArguments(mBundle);
        }
        return fragment;
    }

    @OnItemClick(R.id.notification_list_view)
    void onItemClick(int position) {
        NotificationModel notificationModel = ((NotificationAdapter) notificationListView.getAdapter()).getItem(position);
        presenter.onNotificationItemClicked(notificationModel);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mIsInjected = injectDependency();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeContract.HomeDelegate) {
            homeDelegate = (HomeContract.HomeDelegate) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onStop() {
        presenter.stop();
        super.onStop();
    }

    @Override
    public void onDetach() {
        homeDelegate = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.notification));
        }
        if (!mIsInjected) {
            mIsInjected = injectDependency();
        }
        if(getArguments() != null && getArguments().getInt(EXTRA_NOTIFICATION_ID, -1) != -1) {
            presenter.initializeWithId(getArguments().getInt(EXTRA_NOTIFICATION_ID));
        }else {
            presenter.initialize();
        }

    }

    boolean injectDependency() {
        try {
            DaggerNotificationComponent.builder()
                    .applicationComponent(((AppController) getActivity().getApplication()).getApplicationComponent())
                    .notificationModule(new NotificationModule(this))
                    .build()
                    .inject(this);
            return true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void registerNotificationBroadcastReceiver(String action) {
        IntentFilter intentFilter = new IntentFilter(action);
        getActivity().registerReceiver(mNotificationReceiver, intentFilter);
    }

    @Override
    public void unRegisterNotificationBroadcastReceiver() {
        getActivity().unregisterReceiver(mNotificationReceiver);
    }

    @Override
    public void showNotificationListView() {
        notificationListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setNotificationAdapter(List<NotificationModel> value) {
        NotificationAdapter adapter = new NotificationAdapter(getActivity(), value);
        notificationListView.setAdapter(adapter);
    }

    @Override
    public void hideEmptyTextView() {
        emptyTextView.setVisibility(View.GONE);
    }

    @Override
    public void hideNotificationListView() {
        notificationListView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyTextView() {
        emptyTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingView() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public void showLoadingView() {
        hideLoadingView();
        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);

        if (!transparentProgressDialog.isShowing()) {
            transparentProgressDialog.show();
        }
    }

    @Override
    public void updateNotificationCount(Integer value) {
        homeDelegate.updateNotificationCount(value);
    }

    @Override
    public void showNotificationDetailDialog(final NotificationModel notificationModel) {
        final Dialog dialogNotification = new Dialog(getActivity());
        dialogNotification.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNotification.setContentView(R.layout.dialog_notification);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNotification.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialogNotification.getWindow().setAttributes(lp);
        dialogNotification.setCancelable(false);

        TextView txtId = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_NotificationId);
        TextView txtTitle = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_NotificationTitle);
        TextView txtMessage = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_NotificationMessage);
        TextView txtDateTime = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_Notification_DateTime);
        TextView txtReadDateTime = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_Notification_ReadDateTime);
        Button btn_ok = (Button) dialogNotification.findViewById(R.id.btn_Dialog_NotificationOk);

        String formattedMessage;
        String originalMessage = notificationModel.message;
        String notificationDate = "";
        try {
            // Check message contains mobile number or not
            if (originalMessage.contains("-")) {
                formattedMessage = getString(R.string.number_format, originalMessage.substring(0, originalMessage.indexOf("-"))) + "\n";
                originalMessage = originalMessage.substring(originalMessage.indexOf("-") + 1, originalMessage.length());
                // Check message contains comma separated value or not
                if (originalMessage.contains(",")) {
                    // Convert comma separated value in list
                    List<String> notificationMessageItems = Arrays.asList(originalMessage.split("\\s*,\\s*"));
                    // check list contains all value or not
                    if (notificationMessageItems.size() == 5) {
                        // get all value from list
                        formattedMessage += getString(R.string.transaction_id_format, notificationMessageItems.get(3)) + "\n";
                        formattedMessage += getString(R.string.status_format, notificationMessageItems.get(0)) + "\n";
                        formattedMessage += getString(R.string.company_format, notificationMessageItems.get(1)) + "\n";
                        formattedMessage += getString(R.string.product_format, notificationMessageItems.get(2));
                        notificationDate = notificationMessageItems.get(4);
                    } else {
                        formattedMessage += getOriginalMessage(originalMessage);
                    }
                } else {
                    formattedMessage += getOriginalMessage(originalMessage);
                }
            } else {
                formattedMessage = getOriginalMessage(originalMessage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            formattedMessage = originalMessage;
            notificationDate = "";
        }
        // [END]

        txtId.setText(notificationModel.id);
        txtTitle.setText(notificationModel.title);
        // txtMessage.setText(message);
        txtMessage.setText(formattedMessage);
        if (TextUtils.isEmpty(notificationDate)) {
            txtDateTime.setText(getString(R.string.date_time_format, notificationModel.receiveDateTime));
        } else {
            txtDateTime.setText(getString(R.string.date_time_format, notificationDate));
        }
        txtId.setVisibility(View.GONE);

        /* [START] - Display notification read date */
        if (TextUtils.equals(notificationModel.readFlag, "0")) {
            txtReadDateTime.setText(getString(R.string.read_format, DateTime.getCurrentDateTime()));
        } else {
            txtReadDateTime.setText(getString(R.string.read_format, notificationModel.readDateTime));
        }
        // [END]

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNotification.dismiss();
                presenter.onNotificationPopupOkButtonClicked(notificationModel);
            }
        });

        dialogNotification.show();
    }

    @Override
    public void refreshNotificationListView() {
        if (notificationListView.getAdapter() != null) {
            ((NotificationAdapter) notificationListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private String getOriginalMessage(String message) {
        String originalMessage = message;
        try {
            if (originalMessage.trim().length() > 0) {
                if (originalMessage.toLowerCase().contains("payment id")) {
                    String formattedMessage = originalMessage.substring(0, originalMessage.toLowerCase().indexOf("payment id"));
                    formattedMessage += "\n" + originalMessage.substring(originalMessage.toLowerCase().indexOf("payment id"), originalMessage.length());
                    originalMessage = formattedMessage;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            originalMessage = message;
        }
        return originalMessage;
    }

}
