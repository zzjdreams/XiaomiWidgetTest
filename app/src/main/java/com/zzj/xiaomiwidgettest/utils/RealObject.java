package com.zzj.xiaomiwidgettest.utils;

import android.util.Log;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/16
 * @since 1.0.0
 */
public class RealObject implements IObject{
    private static final String TAG = "RealObject";
    @Override
    public void request() {
//        Log.i(TAG, "request: 我是 RealObject 中的方法");
        System.out.println("request: 我是 RealObject 中的方法");
    }
}
