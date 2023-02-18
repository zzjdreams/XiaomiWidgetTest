package com.zzj.xiaomiwidgettest.utils;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/16
 * @since 1.0.0
 */
public class ObjProxy implements IObject{
    IObject realT;

    public ObjProxy(IObject t) {
        realT = t;
    }

    @Override
    public void request() {
        if (isAllow()) realT.request();
    }

    private boolean isAllow() {
        return true;
    }
}
