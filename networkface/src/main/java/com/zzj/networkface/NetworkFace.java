package com.zzj.networkface;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * RetrofitClient
 * Created by Tamic on 2016-06-15.
 * {@link # https://github.com/NeglectedByBoss/RetrofitClient}
 */
public class NetworkFace {

    /**
     * 连接超时时间
     */
    private static final int DEFAULT_TIMEOUT = 20;
    private BaseApiService apiService;
    private static OkHttpClient okHttpClient;
    public static String baseUrl = Constants.URL;
    private static NetworkFace sNewInstance;

    private static Retrofit retrofit;
    private Cache cache = null;
    private File httpCacheDirectory;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(baseUrl);
    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                    .addNetworkInterceptor(
                            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);


    //====================
    private static class SingletonHolder {
        private static final NetworkFace INSTANCE = new NetworkFace();
    }

    public static NetworkFace getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static NetworkFace getInstance(String url) {

        return new NetworkFace(url);
    }

    public static NetworkFace getInstance(String url, Map<String, String> headers) {
        return new NetworkFace(url, headers);
    }

    //======================================

    private NetworkFace() {
        this(baseUrl, null);
   }


    private NetworkFace(String url) {

        this(url, null);
    }

    private NetworkFace(String url, Map<String, String> headers) {

        if (TextUtils.isEmpty(url)) {
            url = baseUrl;
        }

//        if ( httpCacheDirectory == null) {
//            httpCacheDirectory = new File(mContext.getCacheDir(), "tamic_cache");
//        }
//
//        try {
//            if (cache == null) {
//                cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
//            }
//        } catch (Exception e) {
//            Log.e("OKHttp", "Could not create http cache", e);
//        }
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//                .cookieJar(new NovateCookieManger(context))
//                .cache(cache)
                .addInterceptor(new BaseInterceptor(headers))
//                .addInterceptor(new CaheInterceptor(context))
//                .addNetworkInterceptor(new CaheInterceptor(context))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }

   /**
     * ApiBaseUrl
     *
     * @param newApiBaseUrl
     */
    public static void changeApiBaseUrl(String newApiBaseUrl) {
        baseUrl = newApiBaseUrl;
        builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl);
    }

    /**
     *addcookieJar
     */
    public static void addCookie(Context context) {
        okHttpClient.newBuilder().cookieJar(new NovateCookieManger(context)).build();
        retrofit = builder.client(okHttpClient).build();
    }

    /**
     * ApiBaseUrl
     *
     * @param newApiHeaders
     */
    public static void changeApiHeader(Map<String, String> newApiHeaders) {

        okHttpClient.newBuilder().addInterceptor(new BaseInterceptor(newApiHeaders)).build();
        builder.client(httpClient.build()).build();
    }

    /**
     * create BaseApi  defalte ApiManager
     * @return ApiManager
     */
    public NetworkFace createBaseApi() {
        apiService = create(BaseApiService.class);
        return this;
    }

    public BaseApiService getBaseApi() {
        if (apiService == null){
            apiService = create(BaseApiService.class);
        }
        return apiService;
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public  <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    public Subscription getData(Subscriber<IpResult> subscriber, String ip) {
        return apiService.getData(ip)
               .compose(schedulersTransformer())
                .compose(transformer())
                .subscribe(subscriber);
    }

    public Subscription get(String url, Map parameters, Subscriber<IpResult> subscriber) {

        return apiService.executeGet(url, parameters)
                .compose(schedulersTransformer())
                .compose(transformer())
                .subscribe(subscriber);
    }

    public void post(String url, Map<String, String> parameters, Subscriber<ResponseBody> subscriber) {
        apiService.executePost(url, parameters)
                .compose(schedulersTransformer())
                .compose(transformer())
                .subscribe(subscriber);
    }

    public Subscription json(String url, RequestBody jsonStr, Subscriber<IpResult> subscriber) {

        return apiService.json(url, jsonStr)
                .compose(schedulersTransformer())
                .compose(transformer())
                .subscribe(subscriber);
    }

    public void upload(String url, RequestBody requestBody,Subscriber<ResponseBody> subscriber) {
        apiService.upLoadFile(url, requestBody)
                .compose(schedulersTransformer())
                .compose(transformer())
                .subscribe(subscriber);
    }

    public void download(Context context, String url, final CallBack callBack) {
        apiService.downloadFile(url)
                .compose(schedulersTransformer())
                .compose(transformer())
                .subscribe(new DownSubscriber<ResponseBody>(context, callBack));
    }


    //=======================

    Observable.Transformer schedulersTransformer() {
        return new Observable.Transformer() {


            @Override
            public Object call(Object observable) {
                return ((Observable)  observable).subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }

           /* @Override
            public Observable call(Observable observable) {
                return observable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }*/
        };
    }

    <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) schedulersTransformer();
    }

    public <T> Observable.Transformer<BaseResponse<T>, T> transformer() {

        return new Observable.Transformer() {

            @Override
            public Object call(Object observable) {
                return ((Observable) observable).map(new HandleFuc<T>()).onErrorResumeNext(new HttpResponseFunc<T>());
            }
        };
    }

    public <T> Observable<T> switchSchedulers(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static class HttpResponseFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override public Observable<T> call(Throwable t) {
            return Observable.error(ExceptionHandle.handleException(t));
        }
    }

    private class HandleFuc<T> implements Func1<BaseResponse<T>, T> {
        @Override
        public T call(BaseResponse<T> response) {
            if (!response.isOk()) throw new RuntimeException(response.getCode() + "" + response.getMsg() != null ? response.getMsg(): "");
            return response.getData();
        }
    }


    /**
     * /**
     * execute your customer API
     * For example:
     *  MyApiService service =
     *      RetrofitClient.getInstance(MainActivity.this).create(MyApiService.class);
     *
     *  RetrofitClient.getInstance(MainActivity.this)
     *      .execute(service.lgon("name", "password"), subscriber)
     *     * @param subscriber
     */

    public static <T> T execute(Observable<T> observable ,Subscriber<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return null;
    }


    /**
     * DownSubscriber
     * @param <ResponseBody>
     */
    class DownSubscriber<ResponseBody> extends Subscriber<ResponseBody> {
        CallBack callBack;
        Context mContext;

        public DownSubscriber(Context context, CallBack callBack) {
            this.callBack = callBack;
            this.mContext = context;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (callBack != null) {
                callBack.onStart();
            }
        }

        @Override
        public void onCompleted() {
            if (callBack != null) {
                callBack.onCompleted();
            }
        }

        @Override
        public void onError(Throwable e) {
            if (callBack != null) {
                callBack.onError(e);
            }
        }

        @Override
        public void onNext(ResponseBody responseBody) {
            DownLoadManager.getInstance(callBack).writeResponseBodyToDisk(mContext, (okhttp3.ResponseBody) responseBody);

        }
    }

}
