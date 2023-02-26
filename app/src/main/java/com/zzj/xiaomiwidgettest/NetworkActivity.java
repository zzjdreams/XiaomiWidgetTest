package com.zzj.xiaomiwidgettest;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zzj.networkface.BaseApiService;
import com.zzj.networkface.MyRequest;
import com.zzj.networkface.UserBean;
import com.zzj.xiaomiwidgettest.databinding.ActivityNetworkBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.zzj.networkface.Constants.URL;

public class NetworkActivity extends AppCompatActivity {
    private static final String TAG = "NetworkActivity";
    private Context context = NetworkActivity.this;
    private ActivityNetworkBinding mBinding;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityNetworkBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initListener();
    }

    private void initListener() {
        mBinding.btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "run: 执行okhttp请求");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(URL + "/test?name=123")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: ", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG, "onResponse: " + response.body().string());
                    }
                });

                RequestBody body = new FormBody.Builder()
                        .add("name", "小明")
                        .add("age", "19")
                        .add("time", String.valueOf(System.currentTimeMillis()))
                        .build();
                Request request2 = new Request.Builder()
                        .url(URL)
                        .post(body)
                        .build();
                client.newCall(request2).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: ", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG, "onResponse2: " + response.body().string());
                    }
                });
            }
        });

        mBinding.btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                map.put("id", 10006);
                map.put("name", "刘亦菲");

                OkHttpClient client = new OkHttpClient.Builder()
                        .addNetworkInterceptor(
                                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                        // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                        .build();
                Retrofit retrofit = new Retrofit
                        .Builder()
                        .baseUrl(URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BaseApiService service = retrofit.create(BaseApiService.class);
                service.getData3("小米", 13, System.currentTimeMillis()).enqueue(new retrofit2.Callback<MyRequest<UserBean>>() {
                    @Override
                    public void onResponse(retrofit2.Call<MyRequest<UserBean>> call, retrofit2.Response<MyRequest<UserBean>> response) {
                        Log.i(TAG, "onResponse-body: " + response.body().toString());
                        Log.i(TAG, "onResponse-body-getData: " + response.body().getData().toString());
                        Log.i(TAG, "onResponse-body-getData-getName: " + response.body().getData().getName());
                        Log.i(TAG, "onResponse-body-getData-getAge: " + response.body().getData().getAge());
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MyRequest<UserBean>> call, Throwable t) {

                    }
                });
//                service.getData4(map).enqueue(new retrofit2.Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                        try {
//                            Log.i(TAG, "onResponse: " + response.body().string());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
//
//                    }
//                });
                service.getData5(map).enqueue(new retrofit2.Callback<MyRequest<UserBean>>() {
                    @Override
                    public void onResponse(retrofit2.Call<MyRequest<UserBean>> call, retrofit2.Response<MyRequest<UserBean>> response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        Log.i(TAG, "onResponse-body: " + response.body().toString());
                        Log.i(TAG, "onResponse-body-getData: " + response.body().getData().toString());
                        Log.i(TAG, "onResponse-body-getData-getName: " + response.body().getData().getName());
                        Log.i(TAG, "onResponse-body-getData-getAge: " + response.body().getData().getAge());
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MyRequest<UserBean>> call, Throwable t) {

                    }
                });

                service.getData6("666", map).enqueue(new retrofit2.Callback<MyRequest<UserBean>>() {
                    @Override
                    public void onResponse(retrofit2.Call<MyRequest<UserBean>> call, retrofit2.Response<MyRequest<UserBean>> response) {
                        Log.e(TAG, "onResponse: ===================" );
                        Log.i(TAG, "onResponse-body: " + response.body().toString());
                        Log.i(TAG, "onResponse-body-getData: " + response.body().getData().toString());
                        Log.i(TAG, "onResponse-body-getData-getName: " + response.body().getData().getName());
                        Log.i(TAG, "onResponse-body-getData-getAge: " + response.body().getData().getAge());
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MyRequest<UserBean>> call, Throwable t) {

                    }
                });
            }
        });

        mBinding.btnTest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                map.put("id", 10006);
                map.put("name", "刘亦菲");

                UserBean bean = new UserBean("销毁", 12, System.currentTimeMillis());

                OkHttpClient client = new OkHttpClient.Builder()
                        .addNetworkInterceptor(
                                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                        // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                        .build();
                Retrofit retrofit = new Retrofit
                        .Builder()
                        .baseUrl(URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BaseApiService service = retrofit.create(BaseApiService.class);

                service.postData(map).enqueue(new retrofit2.Callback<MyRequest<UserBean>>() {
                    @Override
                    public void onResponse(retrofit2.Call<MyRequest<UserBean>> call, retrofit2.Response<MyRequest<UserBean>> response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        Log.i(TAG, "onResponse-body: " + response.body().toString());
                        Log.i(TAG, "onResponse-body-getData: " + response.body().getData().toString());
                        Log.i(TAG, "onResponse-body-getData-getName: " + response.body().getData().getName());
                        Log.i(TAG, "onResponse-body-getData-getAge: " + response.body().getData().getAge());
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MyRequest<UserBean>> call, Throwable t) {

                    }
                });
                

                service.postData2(bean).enqueue(new retrofit2.Callback<MyRequest<UserBean>>() {
                    @Override
                    public void onResponse(retrofit2.Call<MyRequest<UserBean>> call, retrofit2.Response<MyRequest<UserBean>> response) {
                        Log.i(TAG, "onResponse: ===================");
                        Log.i(TAG, "onResponse: " + response.toString());
                        Log.i(TAG, "onResponse-body: " + response.body().toString());
                        Log.i(TAG, "onResponse-body-getData: " + response.body().getData().toString());
                        Log.i(TAG, "onResponse-body-getData-getName: " + response.body().getData().getName());
                        Log.i(TAG, "onResponse-body-getData-getAge: " + response.body().getData().getAge());
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MyRequest<UserBean>> call, Throwable t) {

                    }
                });
            }
        });
    }
}