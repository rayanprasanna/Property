package com.universl.realestate.response;

import com.google.gson.annotations.SerializedName;

public class PropertyResponse {
    @SerializedName("id")
    public int id;
    @SerializedName("status")
    public String status;
    @SerializedName("title")
    public String title;
    @SerializedName("district")
    public String district;
    @SerializedName("city")
    public String city;
    @SerializedName("address")
    public String address;
    @SerializedName("category")
    public String category;
    @SerializedName("property_type")
    public String property_type;
    @SerializedName("land_type")
    public String land_type;
    @SerializedName("land_size")
    public String land_size;
    @SerializedName("house_size")
    public String house_size;
    @SerializedName("bed")
    public String bed;
    @SerializedName("bath")
    public String bath;
    @SerializedName("description")
    public String description;
    @SerializedName("price")
    public String price;
    @SerializedName("contact")
    public String contact;
    @SerializedName("post")
    public String post;
    @SerializedName("image_path_1")
    public String image_path_1;
    @SerializedName("image_path_2")
    public String image_path_2;
    @SerializedName("image_path_3")
    public String image_path3;
    @SerializedName("image_path_4")
    public String image_path_4;
    @SerializedName("image_path_5")
    public String image_path_5;
    @SerializedName("user_id")
    public String user_id;

    public PropertyResponse() {
    }

    public PropertyResponse(String image_path_1, String user_id) {
        this.image_path_1 = image_path_1;
        this.user_id = user_id;
    }
}
