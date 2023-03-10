package com.zzj.xiaomiwidgettest.view;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zzj.xiaomiwidgettest.R;
import com.zzj.xiaomiwidgettest.utils.OverLayerUtil;

import java.lang.reflect.InvocationTargetException;


/**
 * Description:
 *
 * @author zzj
 * @date 2023/1/29
 * @since 1.0.0
 */
public class ScreenAnimView extends View {
    private static final String TAG = "ScreenAnimView";
    private static final int DROP_CODE = 0x101;

    private int screenWidth;
    private int screenHeight;
    private int step1;
    private int step2;
    private int divideHigh;
    private Paint mPaint;
    private Bitmap bitmap;
    private Handler handler;
    private int currentHeight = 0;
    private boolean isRunning = false;
    private DevicePolicyManager devicePolicyManager = null;
    private ComponentName screenReceiver  = null;
    private OverLayerUtil.Callback callback;

    public ScreenAnimView(Context context) {
        super(context);
        init(context);
    }

    public ScreenAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCallback(OverLayerUtil.Callback callback) {
        this.callback = callback;
    }

    private void init(Context context){
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        bitmap = getBitmap(context, R.drawable.ic_unknow);
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        setLayerType(LAYER_TYPE_HARDWARE,null);
        getScreenSize(context);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == DROP_CODE){
                    if (currentHeight+40 >= screenHeight) {
//                        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//                        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
//                        wakeLock.acquire(900 );
//
//                        //?????????????????????????????????????????????????????????????????????
//                        //???????????????????????????
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                wakeLock.release();
//                            }
//                        }, 1000);
//                        turnOffScreen();
                        if (callback != null) {
                            callback.invoke();
                        }
                        return;
                    };

                    currentHeight +=currentHeight<divideHigh?step1:step2;
                    invalidate();
                    Log.i(TAG, "screenWidth: " + screenWidth);
                    Log.i(TAG, "currentHeight: " + currentHeight);
                    Message msg1 = handler.obtainMessage();
                    msg1.what = DROP_CODE;
                    handler.sendMessageDelayed(msg1, 50);
//                    isRunning = false;
//                    Log.e(TAG, "handleMessage: ????????????:" + currentHeight);
                }
            }
        };
        Message msg1 = handler.obtainMessage();
        msg1.what = DROP_CODE;
        handler.sendMessageDelayed(msg1, 1000);
    }

    private void turnOffScreen() {
        if (devicePolicyManager != null){
            devicePolicyManager.lockNow();
        }
    }

    private void getScreenSize(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
            screenWidth = windowMetrics.getBounds().width();
            screenHeight = windowMetrics.getBounds().height();
        }else {
            display = windowManager.getDefaultDisplay();
            //??????????????????(????????????)
            screenWidth = display.getWidth();
            //??????????????????(????????????)
            screenHeight = display.getHeight();
        }
        divideHigh = (screenHeight*2/3);
        step1 = divideHigh/31;
        step2 = (screenHeight-divideHigh)/31;
        step1 = Math.max(step1, 1);
        step2 = Math.max(step2, 1);
        Log.i(TAG, "getScreenSize: step1:" + step1);
        Log.i(TAG, "getScreenSize: step2:" + step2);
    }

    private void setDefaultAttr(){

    }

    private Bitmap getAvatar(int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // inJustDecodeBounds???true????????????bitmap??????????????????bitmap?????????
        options.inJustDecodeBounds = true;
        // ??????????????????(??????????????????????????????????????????true????????????????????????)
        BitmapFactory.decodeResource(getResources(), R.drawable.ic_unknow, options);
        // ????????????false??????????????????bitmap
        options.inJustDecodeBounds = false;
        // ????????????????????????????????????
        options.inDensity = options.outWidth;
        options.inTargetDensity = width;
        return BitmapFactory.decodeResource(getResources(), R.drawable.ic_unknow, options);
    }


    public static Bitmap getBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        context.getResources().openRawResource(resId, value);
        options.inTargetDensity = value.density;
        options.inScaled=false;//?????????
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(screenWidth, screenHeight);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.i(TAG, "onMeasure: "+width+":::"+height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: ");
//        canvas.restore();
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        canvas.save();
//        canvas.drawBitmap(bitmap, 0f, 0f, mPaint);
        canvas.drawRect(0, 0, screenWidth, currentHeight, mPaint);
        canvas.drawCircle(screenWidth/2, screenHeight/2, 300,  mPaint);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(TAG, "dispatchTouchEvent: ?????????????????????"+super.dispatchTouchEvent(event));
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
