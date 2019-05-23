package com.example.somsennodejsapp.Retrofit;

import io.reactivex.Observable;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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

    @POST("getaccount")
    @FormUrlEncoded
    Observable<String> getAccount(@Field("unique_id") String unique_id);

    @POST("setaccount")
    @FormUrlEncoded
    Observable<String> setAccount(@Field("unique_id") String unique_id,
                                  @Field("name") String name,
                                  @Field("lastname") String lastname,
                                  @Field("state") String state,
                                  @Field("city") String city);
}
