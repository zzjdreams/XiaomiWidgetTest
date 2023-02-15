package com.zzj.xiaomiwidgettest.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.zzj.xiaomiwidgettest.utils.OverLayerUtil;
import com.zzj.xiaomiwidgettest.view.EyesView;
import com.zzj.xiaomiwidgettest.view.ItemViewTouchListener;
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

    @Override
    public void onCreate() {
        super.onCreate();
        initObs();
        Log.i(TAG, "onCreate: 服务已启动");
    }

    @Override
    public void onDestroy() {
        clearFlag();
        if (mRunnable != null && mHandler != null){
            mHandler.removeCallbacks(mRunnable);
        }
        Log.i(TAG, "onCreate: 服务已销毁");
        super.onDestroy();
    }

    private void initObs() {
        ViewModelWidget.widgetData.observe(this, new Observer<Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
//                    if (!isWorking){
//                        showFloat(new OverLayerUtil.Callback() {
//                            @Override
//                            public void invoke() {
//                                mHandler.postDelayed(mRunnable, 500);
//                            }
//                        });
//                    }
                    showEyes();
                }else {
//                    if (isWorking) {
//                        mHandler.removeCallbacks(mRunnable);
//                    }
                    removeEyes();
                }
            }
        });
    }

    private ScreenAnimView floatRootView;
    private boolean isWorking = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;
    private WindowManager windowManager;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showFloat(OverLayerUtil.Callback callback) {
        if (floatRootView == null) {
            floatRootView = new ScreenAnimView(this);
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
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
        windowManager.addView(floatRootView, layoutParam);
        isWorking = true;
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (floatRootView != null){
                    clearFlag();
                }
            }
        };
        floatRootView.setCallback(callback);
    }

    private EyesView eyesView;
    @SuppressLint("ClickableViewAccessibility")
    private void showEyes() {
        if (eyesView == null) {
            eyesView = new EyesView(this);
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
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
        eyesView.setOnTouchListener(new ItemViewTouchListener(layoutParam, windowManager));
        windowManager.addView(eyesView, layoutParam);
    }

    private void removeEyes() {
        if (windowManager != null && eyesView != null) {
            windowManager.removeView(eyesView);
        }
    }

    private void clearFlag() {
        if (windowManager != null && floatRootView != null) {
            windowManager.removeView(floatRootView);
        }
        floatRootView = null;
        isWorking  =false;
    }

}

