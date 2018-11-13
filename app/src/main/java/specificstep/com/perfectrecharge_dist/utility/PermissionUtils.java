package specificstep.com.perfectrecharge_dist.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;


public class PermissionUtils {

    public static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Gets whether you should show UI with rationale for requesting permissions.
     *
     * @param permissions The permissions your app wants to request.
     * @return Whether you can show permission rationale UI.
     */
    public static boolean shouldShowRequestPermissions(Fragment fragment, String[] permissions) {
        for (String permission : permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }
        return false;
    }


    public static boolean shouldShowRequestPermission(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public static boolean shouldShowRequestPermission(Fragment fragment, String permission) {
        return fragment.shouldShowRequestPermissionRationale(permission);
    }

    public static void requestPermission(Activity activity, String[] permissions, int reqId) {
        ActivityCompat.requestPermissions(activity,
                permissions,
                reqId);
    }

    public static void requestPermission(Fragment fragment, String[] permissions, int reqId) {
        fragment.requestPermissions(permissions, reqId);
    }
}
