package com.boldman.cooperuser.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarImage implements Serializable {

    @SerializedName("car_image_id")
    private int carImageId;

    @SerializedName("car_image_url")
    private String carImageUrl;
}
