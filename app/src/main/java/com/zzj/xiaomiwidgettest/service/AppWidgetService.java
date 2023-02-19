package com.zzj.xiaomiwidgettest.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.zzj.xiaomiwidgettest.MainActivity;
import com.zzj.xiaomiwidgettest.R;
import com.zzj.xiaomiwidgettest.utils.OverLayerUtil;
import com.zzj.xiaomiwidgettest.view.EyesView;
import com.zzj.xiaomiwidgettest.view.ItemViewTouchListener;
import com.zzj.xiaomiwidgettest.view.RemoteViewManger;
import com.zzj.xiaomiwidgettest.view.ScreenAnimView;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/14
 * @since 1.0.0
 */
public class AppWidgetService extends LifecycleService{

    private static final String TAG = "AppWidgetService";

    private RemoteViewManger remoteViewManger1;
    private RemoteViewManger remoteViewManger2;
    private WindowManager windowManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        Log.i(TAG, "onCreate: 服务已启动");
    }

    @Override
    public void onDestroy() {
        if (remoteViewManger1.isAdd()){
            remoteViewManger1.remove();
        }
        if (remoteViewManger2.isAdd()){
            remoteViewManger2.remove();
        }
        Log.i(TAG, "onDestroy: 服务已销毁");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            stopForeground(true);
//        }
        super.onDestroy();
    }

    private void initData(){
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        initRemoteView1();
        initRemoteView2();
        if (!remoteViewManger2.isAdd()){
            remoteViewManger2.add();
        }
        initObs();
    }

    private void initRemoteView1() {
        ScreenAnimView screenAnimView = new ScreenAnimView(this);
        WindowManager.LayoutParams layoutParam = new  WindowManager.LayoutParams();
        layoutParam.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 设置背景为透明
        layoutParam.format = PixelFormat.RGBA_8888;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            layoutParam.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParam.flags |=  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParam.alpha = 0.8f;
        remoteViewManger1 = RemoteViewManger
                .builder()
                .bindView(screenAnimView)
                .setLp(layoutParam)
                .setWindowManager(windowManager)
                .create();
    }

    private void initRemoteView2() {
        EyesView eyesView = new EyesView(this);
        WindowManager.LayoutParams layoutParam = new  WindowManager.LayoutParams();
        layoutParam.width = 1080;
        layoutParam.height = 350;
        // 设置背景为透明
        layoutParam.format = PixelFormat.RGBA_8888;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            layoutParam.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParam.flags =  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        ItemViewTouchListener itemViewTouchListener = new ItemViewTouchListener(layoutParam, windowManager);
        itemViewTouchListener.setClickListener(new ItemViewTouchListener.ClickListener() {
            @Override
            public void onClick() {
                eyesView.changeSkinColor();
            }

            @Override
            public void onLongClick() {

            }
        });
        remoteViewManger2 = RemoteViewManger
                .builder()
                .bindView(eyesView)
                .setLp(layoutParam)
                .setWindowManager(windowManager)
                .setListener(itemViewTouchListener)
                .create();
    }

    private void initObs() {
        ViewModelWidget.widgetData.observe(this, new Observer<Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
//                    if (!remoteViewManger1.isAdd()){
//                        remoteViewManger1.add();
//                    }
                    if (!remoteViewManger2.isAdd()){
                        remoteViewManger2.add();
                    }
                }else {
//                    if (remoteViewManger1.isAdd()){
//                        remoteViewManger1.remove();
//                    }
                    if (remoteViewManger2.isAdd()){
                        remoteViewManger2.remove();
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        startForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    String notificationId = "channelId";
    String notificationName = "channelName";
    private void startForeground(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 需要添加管道，否则直接闪退
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("悬浮窗服务启动")
                .setContentText("悬浮窗服务正在运行...");
        //设置Notification的ChannelID,否则不能正常显示

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    public static class BindService implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: 线程连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceConnected: 线程断开");
        }
    }

}

