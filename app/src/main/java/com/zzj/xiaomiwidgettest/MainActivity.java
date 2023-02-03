package com.zzj.xiaomiwidgettest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.zzj.xiaomiwidgettest.databinding.ActivityMainBinding;
import com.zzj.xiaomiwidgettest.receiver.MyAppWidget;
import com.zzj.xiaomiwidgettest.utils.OverLayerUtil;
import com.zzj.xiaomiwidgettest.view.ItemViewTouchListener;
import com.zzj.xiaomiwidgettest.view.ScreenAnimView;
import com.zzj.xiaomiwidgettest.view.TestView;

public class MainActivity extends AppCompatActivity {
    private MyAppWidget widget;
    private static final String ACTION = "com.zzj.widget";
    private Button btn_send;
    private ActivityMainBinding mainBinding;
    private DevicePolicyManager devicePolicyManager = null;
    private ComponentName screenReceiver  = null;
    private static final int SCREEN_OFF = 0;
    private static final int SCREEN_ON = 1;
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        registerReceiver();

        btn_send = findViewById(R.id.btn_test_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION);
                intent.setAction(ACTION);
                sendBroadcast(intent);
            }
        });
        mainBinding.btnTestCreateFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentFloat();
            }
        });

        mainBinding.btnTestCreateFloatSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainBinding.btnTestResetFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 点击了重置按钮");
            }
        });

        screenReceiver = new ComponentName(MainActivity.this, ScreenOnAndOffReceiver.class);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

//        Intent intent2 = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//        intent2.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,  screenReceiver);
//        intent2.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"使用亮灭屏功能");
//
//        startActivityForResult(intent2, 0);

    }

    public class ScreenOnAndOffReceiver extends DeviceAdminReceiver {
        private ScreenOnAndOffReceiver() {

        }
        private void showToast(Context context, String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            showToast(context,
                    "设备管理器启用");
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            showToast(context,
                    "设备管理器禁用");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isOpen();
    }


    private void isOpen() {
        if(devicePolicyManager.isAdminActive(screenReceiver)){

            Toast.makeText(MainActivity.this,"设备已被激活",
                    Toast.LENGTH_LONG).show();

        }else{

            Toast.makeText(MainActivity.this,"设备没有被激活",
                    Toast.LENGTH_LONG).show();

        }
    }



    private void registerReceiver(){
        widget = new MyAppWidget();
        Intent intent = new Intent();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(widget, filter);

    }

    private void unregisterReceiver(){
        unregisterReceiver(widget);
    }
    ScreenAnimView floatRootView = null;
    @SuppressLint("ClickableViewAccessibility")
    private void showCurrentFloat(){
        if (floatRootView == null) {
            WindowManager.LayoutParams layoutParam = new  WindowManager.LayoutParams();
            layoutParam.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            /** 必须是这个flags WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE */
            layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    // 加上这句话悬浮窗不拦截事件
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;;

            // 新建悬浮窗控件
            floatRootView = new ScreenAnimView(this);
//            floatRootView = new TestView(this);
            layoutParam.x = 0;
            layoutParam.y = 0;
            // 设置背景为透明
            layoutParam.format = PixelFormat.RGBA_8888;
            layoutParam.alpha = 0.5f;
//            floatRootView.setOnTouchListener(new ItemViewTouchListener(layoutParam, getWindowManager()));
            // 将悬浮窗控件添加到WindowManager
            getWindowManager().addView(floatRootView, layoutParam);
            floatRootView.setCallback(new OverLayerUtil.Callback() {
                @Override
                public void invoke() {
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getWindowManager().removeView(floatRootView);
                            floatRootView = null;
                        }
                    }, 500);
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver();
        super.onDestroy();
    }
}