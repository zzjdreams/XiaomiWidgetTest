package com.zzj.xiaomiwidgettest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.zzj.xiaomiwidgettest.R;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/2
 * @since 1.0.0
 */
public class TestView extends LinearLayout {
    public TestView(Context context) {
        super(context);
        init(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_recorder_remote_view, (ViewGroup) getRootView());
    }
}
