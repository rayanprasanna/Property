package com.universl.realestate.utils;

import com.universl.realestate.response.PropertyResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("insert_property.php")
    Call<PropertyResponse> uploadMultiple(
            @Part("description") RequestBody description,
            @Part("size") RequestBody size,
            @Part("status") RequestBody status,
            @Part("title") RequestBody title,
            @Part("district")RequestBody district,
            @Part("city")RequestBody city,
            @Part("address")RequestBody address,
            @Part("category")RequestBody category,
            @Part("property_type")RequestBody property_type,
            @Part("land_type")RequestBody land_type,
            @Part("land_size")RequestBody land_size,
            @Part("house_size")RequestBody house_size,
            @Part("bed")RequestBody bed,
            @Part("bath")RequestBody bath,
            @Part("description")RequestBody descriptions,
            @Part("price")RequestBody price,
            @Part("contact")RequestBody contact,
            @Part("user_id")RequestBody user_id,
            @Part("post")RequestBody post,
            @Part List<MultipartBody.Part> files);
}
