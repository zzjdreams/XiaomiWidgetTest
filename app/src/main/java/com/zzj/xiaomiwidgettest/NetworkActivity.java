package com.zzj.xiaomiwidgettest;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zzj.networkface.BaseApiService;
import com.zzj.networkface.CallBack;
import com.zzj.networkface.MyRequest;
import com.zzj.networkface.RetrofitClient;
import com.zzj.networkface.UserBean;
import com.zzj.xiaomiwidgettest.databinding.ActivityNetworkBinding;

import java.io.File;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zzj.networkface.Constants.URL;

public class NetworkActivity extends AppCompatActivity {
    private static final String TAG = "NetworkActivity";
    private static final int REQUEST_CODE = 0x100;
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

        mBinding.btnTest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitClient.getInstance(context)
                        .getBaseApi()
                        .obtainMsg("name")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MyRequest<UserBean>>() {
                            @Override
                            public void onCompleted() {
                                Log.i(TAG, "onCompleted: " + Thread.currentThread().getName());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: ", e);
                            }

                            @Override
                            public void onNext(MyRequest<UserBean> userBeanMyRequest) {
                                Log.i(TAG, "onNext: " + userBeanMyRequest.toString());
                                Log.i(TAG, "onNext-getData: " + userBeanMyRequest.getData().toString());
                            }
                        });

                Map<String, Object> map = new HashMap<>(8);
                map.put("id", 10006);
                map.put("name", "刘亦菲");
                RetrofitClient.getInstance(context)
                        .getBaseApi()
                        .obtainMsg(map)
                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MyRequest<UserBean>>() {
                            @Override
                            public void onCompleted() {
                                Log.i(TAG, "onCompleted2: " + Thread.currentThread().getName());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError2: ", e);
                            }

                            @Override
                            public void onNext(MyRequest<UserBean> userBeanMyRequest) {
                                Log.i(TAG, "onNext: ==============");
                                Log.i(TAG, "onNext2: " + userBeanMyRequest.toString());
                                Log.i(TAG, "onNext2-getData: " + userBeanMyRequest.getData().toString());
                            }
                        });
            }
        });

        mBinding.btnTest5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(dir, "app-debug.apk");
                MediaType mediaType = MediaType.parse("application/octet-stream");//设置类型，类型为八位字节流
                RequestBody requestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体
                FormBody formBody = new FormBody.Builder()
                        .add("name", "小红")
                        .add("age", "13")
                        .build();
                MultipartBody body = new MultipartBody.Builder()
                        .addPart(formBody)
                        .addFormDataPart("registerTime", String.valueOf(System.currentTimeMillis()))
//                        .addPart(requestBody)
                        .build();
                MultipartBody.Part part = MultipartBody.Part.create(body);
                RetrofitClient.getInstance(context)
                        .getBaseApi()
                        .postMsg(part)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MyRequest<UserBean>>() {
                            @Override
                            public void onCompleted() {
                                Log.i(TAG, "onCompleted: " + Thread.currentThread().getName());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: ", e);
                            }

                            @Override
                            public void onNext(MyRequest<UserBean> userBeanMyRequest) {
                                Log.i(TAG, "onNext: " + userBeanMyRequest.toString());
                                Log.i(TAG, "onNext-getData: " + userBeanMyRequest.getData().toString());
                            }
                        });

                Map<String, Object> map = new HashMap<>(8);
                map.put("id", 10006);
                map.put("name", "刘亦菲");
                RetrofitClient.getInstance(context)
                        .getBaseApi()
                        .postMsg(map)
                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MyRequest<UserBean>>() {
                            @Override
                            public void onCompleted() {
                                Log.i(TAG, "onCompleted2: " + Thread.currentThread().getName());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError2: ", e);
                            }

                            @Override
                            public void onNext(MyRequest<UserBean> userBeanMyRequest) {
                                Log.i(TAG, "onNext2: " + userBeanMyRequest.toString());
                                Log.i(TAG, "onNext2-getData: " + userBeanMyRequest.getData().toString());
                            }
                        });
            }
        });

        mBinding.btnTest6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFile();
            }
        });

        mBinding.btnTest7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitClient.getInstance(context)
                        .createBaseApi()
                        .download("download", new CallBack() {
                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onSucess(String path, String name, long fileSize) {
                                Log.i(TAG, "onSucess path: " + path);
                                Log.i(TAG, "onSucess name: " + name);
                                Log.i(TAG, "onSucess fileSize: " + fileSize);
                            }
                        });
            }
        });
    }


    // 打开系统的文件选择器
    public void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        this.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            if (data == null) {
                // 用户未选择任何文件，直接返回
                return;
            }
            Uri uri = data.getData(); // 获取用户选择文件的URI
            // 通过ContentProvider查询文件路径
            ContentResolver resolver = this.getContentResolver();
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor == null) {
                // 未查询到，说明为普通文件，可直接通过URI获取文件路径
                String path = uri.getPath();
                uploadFile(path);
                return;
            }
            if (cursor.moveToFirst()) {
                // 多媒体文件，从数据库中获取文件的真实路径
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex("_data"));
                uploadFile(path);
            }

            cursor.close();
        }
    }

    private void uploadFile(String path) {
        Log.i(TAG, "uploadFile path: " + path);
        File file = new File(path);
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-dat"), file);
        RetrofitClient.getInstance(context)
                .createBaseApi()
                .upload("uploadFile", body, new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: 上传成功");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Log.i(TAG, "onNext: " + responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}