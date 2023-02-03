package com.zzj.xiaomiwidgettest.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/1/29
 * @since 1.0.0
 */
public class OverLayerUtil {
    public static final int REQUEST_FLOAT_CODE = 0X20;

    /**\
     * 判断服务是否存活
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (TextUtils.isEmpty(serviceName)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningServices = (ArrayList<ActivityManager.RunningServiceInfo>)
                myManager.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo info: runningServices){
            if (info.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断悬浮窗权限权限
     */
    public static boolean commonROMPermissionCheck(Context context) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz  = Settings.class;
                Method canDrawOverlays =
                        clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                Log.e("ServiceUtils", Log.getStackTraceString(e));
            }
        }
        return result;
    }

    /**
     * 检查悬浮窗权限是否开启
     */
    public static void checkSuspendedWindowPermission(Activity context, Callback callback) {
        if (commonROMPermissionCheck(context)) {
            callback.invoke();
        } else {
            Toast.makeText(context, "请开启悬浮窗权限", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:${context.packageName}"));
            context.startActivityForResult(intent, REQUEST_FLOAT_CODE);
        }
    }

//    /**
//     * 检查无障碍服务权限是否开启
//     */
//    public static void checkAccessibilityPermission(Activity context, Callback callback) {
//        if (isServiceRunning(context, WorkAccessibilityService::class.java.canonicalName)) {
//            callback.invoke();
//        } else {
//            accessibilityToSettingPage(context)
//        }
//    }

    public static interface Callback{
        void invoke();
    }
}


