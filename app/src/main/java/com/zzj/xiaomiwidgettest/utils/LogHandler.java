package com.zzj.xiaomiwidgettest.utils;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/16
 * @since 1.0.0
 */
public class LogHandler implements InvocationHandler {
    private static final String TAG = "LogHandler";

    IObject realT;

    public LogHandler(IObject t) {
        realT = t;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        // request方法时，进行校验
        if (method.getName().equals("request") && !isAllow()) {
//            Log.e(TAG, "invoke: LogHandler 检索无方法或不允许调用" );
            System.out.println("invoke: LogHandler 检索无方法或不允许调用");
            return null;
        }
//        Log.e(TAG, "invoke: LogHandler 调用成功" );
        System.out.println("invoke: LogHandler 调用成功" );
        return method.invoke(realT, args);
    }

    private boolean isAllow() {
        return true;
    }

}
