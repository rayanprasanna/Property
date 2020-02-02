package com.universl.realestate.response;

import com.google.gson.annotations.SerializedName;

public class HitAdsResponse {
    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String title;
    @SerializedName("description")
    public String description;
    @SerializedName("contact")
    public String contact;
}
