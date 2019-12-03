package com.boldman.cooperuser.Model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarImage {

    @SerializedName("car_image_id")
    private int car_image_id;

    @SerializedName("car_image_url")
    private String car_image_url;
}
