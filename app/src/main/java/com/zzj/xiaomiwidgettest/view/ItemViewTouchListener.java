package com.zzj.xiaomiwidgettest.view;

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
    private WindowManager.LayoutParams wl;
    private WindowManager windowManager;

    private int x = 0;
    private int y = 0;

    public ItemViewTouchListener(WindowManager.LayoutParams wl, WindowManager windowManager) {
        this.wl = wl;
        this.windowManager = windowManager;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getRawX();
                y = (int) event.getRawY();
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

        }
        return false;
    }
}
