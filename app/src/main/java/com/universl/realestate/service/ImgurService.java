package com.universl.realestate.service;

import com.universl.realestate.response.ImageResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ImgurService {
    @Multipart
    @Headers({
            "Authorization: Client-ID b890b896652e432"
    })
    @POST("image")
    Call<ImageResponse> postImage(
            @Query("title") String title,
            @Query("description") String description,
            @Query("album") String albumId,
            @Query("account_url") String username,
            @Part MultipartBody.Part file);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.imgur.com/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}