package com.universl.realestate.response;

import com.google.gson.annotations.SerializedName;

public class DistrictResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("name_si")
    private String name_si;
    @SerializedName("district_id")
    private int district_id;
    @SerializedName("name_en")
    private String name_en;

    public String getName_en() {
        return name_en;
    }

    public int getId() {
        return id;
    }

    public String getName_si() {
        return name_si;
    }

    public int getDistrict_id() {
        return district_id;
    }
}
