package com.zzj.xiaomiwidgettest.view;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/1/29
 * @since 1.0.0
 */
public class ItemViewTouchListener implements View.OnTouchListener {
    private final long CLICK_LEVEL_TIME = 3000L;
    private final int MOVE_LEVEL_DISTANCE = 50;
    private WindowManager.LayoutParams wl;
    private WindowManager windowManager;

    private int x = 0;
    private int y = 0;

    private int lastX = 0;
    private int lastY = 0;

    private long clickTime;
    private ClickListener clickListener;

    public ItemViewTouchListener(WindowManager.LayoutParams wl, WindowManager windowManager) {
        this.wl = wl;
        this.windowManager = windowManager;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                clickTime = System.currentTimeMillis();
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) event.getRawX();
                int nowY = (int) event.getRawY();
                int movedX = nowX - x;
                int movedY = nowY - y;
                x = nowX;
                y = nowY;
                wl.x += movedX;
                wl.y += movedY;

            //更新悬浮球控件位置
            windowManager.updateViewLayout(v, wl);
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getRawX();
                int upY = (int) event.getRawY();
                if (Math.abs(lastX - upX) < MOVE_LEVEL_DISTANCE && Math.abs(lastY - upY) < MOVE_LEVEL_DISTANCE && clickListener != null){
                    long obtainTime = System.currentTimeMillis();
                    if (Math.abs(obtainTime - clickTime) < CLICK_LEVEL_TIME){
                        clickListener.onClick();
                    }else {
                        clickListener.onLongClick();
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        /**
         * 点击效果
         */
        void onClick();

        /**
         * 长按效果
         */
        void onLongClick();
    }
}
