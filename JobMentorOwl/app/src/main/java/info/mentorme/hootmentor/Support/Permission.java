package info.mentorme.hootmentor.Support;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by psun on 2017-11-02.
 */

public class Permission {
    public static void requestRecordAudioPermission(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed

            if (checkSelfPermission(context, requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(activity, new String[]{requiredPermission}, 101);
            }
        }
    }

}
