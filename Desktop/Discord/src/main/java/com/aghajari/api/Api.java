package com.aghajari.api;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface Api {

    @Headers({"Content-Type: application/json"})
    @POST("api/user/login")
    Call<ResponseBody> login(@Body String json);

    @Headers({"Content-Type: application/json"})
    @POST("api/user/register")
    Call<ResponseBody> register(@Body String json);

    @Multipart
    @POST("uploadAvatar")
    Call<ResponseBody> uploadAvatar(@Header("auth-token") String token,
                                    @Part MultipartBody.Part image);

    @Multipart
    @POST("uploadimage")
    Call<ResponseBody> uploadImage(@Header("auth-token") String token, @Header("filename") String name,
                                    @Part MultipartBody.Part image);

    @Headers({"Content-Type: application/json"})
    @POST("api/user/searchusername")
    Call<ResponseBody> search(@Header("auth-token") String token, @Body String json);

    @Headers({"Content-Type: application/json"})
    @POST("api/user/changeusername")
    Call<ResponseBody> changeUsername(@Header("auth-token") String token, @Body String json);

    @Headers({"Content-Type: application/json"})
    @POST("api/user/changepass")
    Call<ResponseBody> changepass(@Header("auth-token") String token, @Body String json);
}