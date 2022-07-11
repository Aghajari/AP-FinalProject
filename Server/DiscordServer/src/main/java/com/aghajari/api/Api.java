package com.aghajari.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface Api {

    @Headers({"Content-Type: application/json"})
    @POST("api/user/getuserinfo")
    Call<ResponseBody> auth(@Header("auth-token") String token);


    @Headers({"Content-Type: application/json"})
    @POST("api/user/getinfoByIds")
    Call<ResponseBody> findInfo(@Header("auth-token") String token, @Body String json);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<ResponseBody> bot(@Url String link, @Body String json);
}