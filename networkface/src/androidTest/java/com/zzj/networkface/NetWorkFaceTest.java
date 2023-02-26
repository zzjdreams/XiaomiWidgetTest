package com.zzj.networkface;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/24
 * @since 1.0.0
 */
@RunWith(AndroidJUnit4.class)
public class NetWorkFaceTest {

    private static final String URL = "http://localhost:3000";

    CountDownLatch countDownLatch = new CountDownLatch(1);

    public Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testObtainContext(){
        // Context of the app under test.
        Assert.assertNotNull(getContext());
    }

    @Test
    public void testConnect(){
        try {
            countDownLatch.await();
            RetrofitClient.getInstance(getContext())
                    .createBaseApi()
                    .getMsg(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onError(Throwable e) {
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onNext(String ipResult) {
                            System.out.println(ipResult);
                        }
                    }, "123");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testOkhttp(){


        try {
            countDownLatch.await();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            Response response = client.newCall(request).execute();
            countDownLatch.countDown();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
