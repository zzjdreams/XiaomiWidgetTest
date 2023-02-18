package com.zzj.xiaomiwidgettest.view;

import android.view.View;
import android.view.WindowManager;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/18
 * @since 1.0.0
 */
public class RemoteViewManger implements IRemoteView{

    private final ViewBuilder builder;
    private boolean isAdd = false;

    public RemoteViewManger(ViewBuilder builder) {
        this.builder = builder;
    }

    public static ViewBuilder builder(){
        return new ViewBuilder();
    }


    @Override
    public boolean add() {
        if (builder.windowManager == null){
            return false;
        }
        if (builder.lp == null){
            return false;
        }
        if (builder.view == null){
            return false;
        }
        if (builder.listener != null){
            builder.view.setOnTouchListener(builder.listener);
        }
        builder.windowManager.addView(builder.view, builder.lp);
        isAdd = true;
        return true;
    }

    @Override
    public boolean remove() {
        if (builder.windowManager == null || builder.view == null){
            return false;
        }
        builder.windowManager.removeView(builder.view);
        isAdd = false;
        return true;
    }

    @Override
    public boolean isAdd() {
        return isAdd;
    }

    public static class ViewBuilder{
        private WindowManager windowManager;
        private WindowManager.LayoutParams lp;
        private View.OnTouchListener listener;
        private View view;

        public WindowManager getWindowManager() {
            return windowManager;
        }

        public ViewBuilder setWindowManager(WindowManager windowManager) {
            this.windowManager = windowManager;
            return this;
        }

        public WindowManager.LayoutParams getLp() {
            return lp;
        }

        public ViewBuilder setLp(WindowManager.LayoutParams lp) {
            this.lp = lp;
            return this;
        }

        public View.OnTouchListener getListener() {
            return listener;
        }

        public ViewBuilder setListener(View.OnTouchListener listener) {
            this.listener = listener;
            return this;
        }

        public ViewBuilder bindView(View v) {
           view = v;
            return this;
        }

        public RemoteViewManger create(){

            return new RemoteViewManger(this);
        }
    }
}
