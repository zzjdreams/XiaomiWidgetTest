package com.zzj.xiaomiwidgettest.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.zzj.xiaomiwidgettest.R;
import com.zzj.xiaomiwidgettest.service.AppWidgetService;
import com.zzj.xiaomiwidgettest.service.ViewModelWidget;
import com.zzj.xiaomiwidgettest.utils.OverLayerUtil;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/1/16
 * @since 1.0.0
 */
public class MyAppWidget extends AppWidgetProvider {
    private static final String TAG = "MyAppWidget";
    private static boolean isOff = false;
    private static final String ACTION = "com.zzj.widget";
    private RemoteViews views;
    private Intent serviceIntent;

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int[] appWidgetIds) {
        if (views == null) {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_recorder_remote_view
                    // 这个layout就是我们之前定义的initiallayout
            );
        }
//        startService(context);
        views.setOnClickPendingIntent(R.id.rl_layout, getPendingIntent(context, R.id.id_iv_switch_status));
        updateWidget(context, appWidgetManager, appWidgetIds, isOff);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager,
                              int[] appWidgetIds, boolean isOff) {
        if (views == null) {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_recorder_remote_view
                    // 这个layout就是我们之前定义的initiallayout
            );
        }
        startService(context);
        System.out.println("isOff: " + isOff);
        if (isOff) {
            // 更新里面某一个子view值
            CharSequence tvTurnOn = context.getString(R.string.turn_on);
            // Construct the RemoteViews object
            views.setTextViewText(R.id.id_tv_status, tvTurnOn);
            views.setImageViewResource(R.id.id_iv_switch_status, R.drawable.ic_turn_on);
        } else {
            // 更新里面某一个子view值
            CharSequence tvTurnOff = context.getString(R.string.turn_off);
            // Construct the RemoteViews object
            views.setTextViewText(R.id.id_tv_status, tvTurnOff);
            views.setImageViewResource(R.id.id_iv_switch_status, R.drawable.ic_turn_off);
        }
        ViewModelWidget.widgetData.setValue(isOff);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    private void startService(Context context) {
        if (!OverLayerUtil.isServiceRunning(context, AppWidgetService.class.getCanonicalName())) {
            if (serviceIntent == null) {
                serviceIntent = new Intent(context, AppWidgetService.class);
            }
            Log.i(TAG, "startService: 创建线程");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(new Intent(context, AppWidgetService.class));
//            }else {
            context.startService(serviceIntent);
//            }
//            context.bindService(new Intent(context, AppWidgetService.class), new AppWidgetService.BindService(), 0);
        } else {
            Log.i(TAG, "startService: 线程已存在");
        }
    }

    private PendingIntent getPendingIntent(Context context, int resID) {
        Intent intent = new Intent(context, MyAppWidget.class);
//        intent.setClass(context, MyAppWidget.class);//此时这句代码去掉
        intent.setAction(ACTION);
        //设置data域的时候，把控件id一起设置进去，
        // 因为在绑定的时候，是将同一个id绑定在一起的，所以哪个控件点击，发送的intent中data中的id就是哪个控件的id
        intent.setData(Uri.parse("id:" + resID));
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateAppWidget(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate: ");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: " + intent.getAction());
        String action = intent.getAction();
        if (action.equals(ACTION)) {
            int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, MyAppWidget.class));
            isOff = !isOff;
            updateWidget(context, AppWidgetManager.getInstance(context), appWidgetIds, isOff);
        }

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "onEnabled: ");
        if (!OverLayerUtil.isServiceRunning(context, AppWidgetService.class.getCanonicalName())) {
            startService(context);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.d(TAG, "onAppWidgetOptionsChanged: ");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "onDeleted: ");
    }

    @Override
    public void onDisabled(Context context) {
        if (serviceIntent != null) {
            context.stopService(serviceIntent);
            serviceIntent = null;
        }
        super.onDisabled(context);
        Log.d(TAG, "onDisabled: ");
    }


    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Log.d(TAG, "onRestored: ");
    }


}
