package com.zzj.networkface;


import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Ｔａｍｉｃ on 2016-07-08.
 * {@link # https://github.com/NeglectedByBoss/RetrofitClient}
 *
 * 使用
 * POST 请求：
 *  使用 @Field 和 @FieldMap 标记的变量需要在方法顶使用 @FormUrlEncoded 进行标记
 *  使用 @Body 标记的 RequestBody 在方法顶不能使用 @FormUrlEncoded 进行标记
 *  <ul>
 *      <ol>@Multipart              表示请求实体是一个支持文件上传的表单，需要配合@Part和@PartMap使用，适用于文件上传</ol>
 *      <ol>@Part                       用于表单字段，适用于文件上传的情况，@Part支持三种类型：RequestBody、MultipartBody.Part、任意类型</ol>
 *      <ol>@PartMap               用于多文件上传， 与@FieldMap和@QueryMap的使用类</ol>
 *  </ul>
 *
 */
public interface BaseApiService {

    public static final String Base_URL = "http://localhost:3000";
    /**
     *普通写法
     */
    @GET("service/getIpInfo.php")
    Observable<BaseResponse<IpResult>> getData(@Query("ip") String ip);


    @GET("{url}")
    Observable<BaseResponse<Object>> executeGet(
            @Path("url") String url,
            @QueryMap Map<String, String> maps
           );


    @POST("{url}")
    Observable<ResponseBody> executePost(
            @Path("url") String url,
          //  @Header("") String authorization,
            @QueryMap Map<String, String> maps);

    @POST("{url}")
    Observable<ResponseBody> json(
            @Path("url") String url,
            @Body RequestBody jsonStr);

    @Multipart
    @POST("{url}")
    Observable<ResponseBody> upLoadFile(
            @Path("url") String url,
            @Part("image\"; filename=\"image.jpg") RequestBody requestBody);

    @POST("{url}")
    Call<ResponseBody> uploadFiles(
            @Path("url") String url,
            @Path("headers") Map<String, String> headers,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> maps);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    //==================================================

    /**
     * 获取 GET 消息
     * @return
     */
    @GET("/host")
    Observable<MyRequest<UserBean>> obtainMsg(@Query("name") String name);

    @GET("/host")
    Observable<MyRequest<UserBean>> obtainMsg(@QueryMap Map<String, Object> map);

    /**
     * 发送 POST 消息
     */
    @Multipart
    @POST("/host")
    Observable<MyRequest<UserBean>> postMsg(@Part("name")MultipartBody.Part part);

    @FormUrlEncoded
    @POST("/host")
    Observable<MyRequest<UserBean>> postMsg(@FieldMap Map<String, Object> map);


    //======================  normal retrofit =======================
    @GET("/user")
    Call<MyRequest<UserBean>> getData3(@Query("name") String name, @Query("age") int age, @Query("date") long registerTime);
    @GET("/user")
    Call<ResponseBody> getData4(@QueryMap Map<String, Object> map);
    @GET("/user")
    Call<MyRequest<UserBean>> getData5(@QueryMap Map<String, Object> map);
    @GET("/user/{path}")
    Call<MyRequest<UserBean>> getData6(@Path("path")String nextPath, @QueryMap Map<String, Object> map);
    @FormUrlEncoded
    @POST("/user")
    Call<MyRequest<UserBean>> postData(@FieldMap Map<String, Object> map);
    @POST("/user")
    Call<MyRequest<UserBean>> postData2(@Body UserBean bean);

}
