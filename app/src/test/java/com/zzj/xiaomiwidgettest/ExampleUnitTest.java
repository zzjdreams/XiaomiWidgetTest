package com.zzj.xiaomiwidgettest;

import com.zzj.xiaomiwidgettest.rxjava.RxJavaTest;
import com.zzj.xiaomiwidgettest.utils.IObject;
import com.zzj.xiaomiwidgettest.utils.LogHandler;
import com.zzj.xiaomiwidgettest.utils.RealObject;

import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    public boolean checkStraightLine(int[][] coordinates) {
        for(int i = 2; i < coordinates.length; i++){
            if ((coordinates[i][1]-coordinates[0][1])*(coordinates[i-1][0]-coordinates[0][0]) != (coordinates[i][0]-coordinates[0][0])*(coordinates[i-1][1]-coordinates[0][1])){
                return false;
            }
        }
        return true;
    }

    @Test
    public void testProxy() {
        IObject proxy = (IObject) Proxy.newProxyInstance(
                IObject.class.getClassLoader(),
                new Class[]{IObject.class},
                new LogHandler(new RealObject()));
        proxy.request(); // ObjProxyHandler的invoke方法会被调用

    }

    @Test
    public void testRxjava1() {
        RxJavaTest.INSTANCE.test1();
    }

    @Test
    public void testRxjava2() {
        RxJavaTest.INSTANCE.testJust();
    }

    @Test
    public void testRxjava3() {
        RxJavaTest.INSTANCE.testFrom();
    }

    @Test
    public void testRxjava4() {
        RxJavaTest.INSTANCE.testDefer();
    }

    @Test
    public void testRxjava5() {
        RxJavaTest.INSTANCE.testError();
    }

    @Test
    public void testRxjava6() {
        RxJavaTest.INSTANCE.testInterval();
    }

    @Test
    public void testRxjava7() {
        RxJavaTest.INSTANCE.testRange();
    }

    @Test
    public void testRxjava8() {
        RxJavaTest.INSTANCE.testMap();
    }

    @Test
    public void testRxjava9() {
        RxJavaTest.INSTANCE.testFlatMap();
    }

    @Test
    public void testRxjava10() {
        RxJavaTest.INSTANCE.testGroupBy();
    }

    @Test
    public void testRxjava11() {
        RxJavaTest.INSTANCE.testBuffer();
    }

    @Test
    public void testRxjava12() {
        RxJavaTest.INSTANCE.testScan();
    }

    @Test
    public void testRxjava13() {
        RxJavaTest.INSTANCE.testWindow();
    }
}