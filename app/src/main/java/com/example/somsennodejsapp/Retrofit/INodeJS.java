package com.example.somsennodejsapp.Retrofit;

import io.reactivex.Observable;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface INodeJS {
    @POST("register")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("email") String email,
                                    @Field("phone") String name,
                                    @Field("password")String password);

    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email,
                                    @Field("password")String password);

    @POST("getuserinfo")
    @FormUrlEncoded
    Observable<String> getUserInfo(@Header("Authorization") String token,
                                   @Field("unique_id") String unique_id);

    @POST("setuserinfo")
    @FormUrlEncoded
    Observable<String> setUserInfo(@Header("Authorization") String token,
                                   @Field("unique_id") String unique_id,
                                   @Field("name") String name,
                                   @Field("lastname") String lastname,
                                   @Field("state") String state,
                                   @Field("city") String city);

    @POST("getnearestbiz")
    @FormUrlEncoded
    Observable<String> getNearestBiz(@Header("Authorization") String token,
                                     @Field("lat") Double lat,
                                     @Field("lon") Double lon);
}
