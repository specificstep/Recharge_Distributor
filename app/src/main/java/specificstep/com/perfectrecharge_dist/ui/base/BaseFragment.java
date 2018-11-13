package specificstep.com.perfectrecharge_dist.ui.base;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import specificstep.com.perfectrecharge_dist.R;

public class BaseFragment extends Fragment {

    public void showDialog(String title, String message) {
        showDialog(title, message, null);
    }

    public void showDialog(String title, String message, final DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onClick(dialogInterface, i);
                        }
                    }
                }).create().show();
    }

    public void showInvalidAccessTokenPopup() {
        if(getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showInvalidAccessTokenPopup();
        }
    }
}
