package com.example.collection.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;

import java.util.ArrayList;

public class PermissionUtils {

    public static boolean hasPermission(Activity activity, String permission) {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(Activity activity, String[] permission, int requestCode) {
        ArrayList<String> reqs = new ArrayList<>();
        for (String permissionI : permission) {
            if (hasPermission(activity, permissionI))
                reqs.add(permissionI);
        }
        if (reqs.size() > 0)
            activity.requestPermissions(reqs.toArray(new String[0]), requestCode);
    }

    public static boolean shouldShowRational(Activity activity, String permission) {
        return activity.shouldShowRequestPermissionRationale(permission);
    }

    public static boolean shouldAskForPermission(Activity activity, String permission) {
        return !hasPermission(activity, permission) &&
                (!hasAskedForPermission(activity, permission) ||
                        shouldShowRational(activity, permission));
    }

    public static void goToAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static boolean hasAskedForPermission(Activity activity, String permission) {
        return PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getBoolean(permission, false);
    }

    public static void markedPermissionAsAsked(Activity activity, String permission) {
        PreferenceManager
                .getDefaultSharedPreferences(activity)
                .edit()
                .putBoolean(permission, true)
                .apply();
    }
}
