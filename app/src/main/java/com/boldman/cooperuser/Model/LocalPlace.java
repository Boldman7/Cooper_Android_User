package com.boldman.cooperuser.Model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocalPlace {

    @SerializedName("id")
    int id;

    @SerializedName("type_id")
    int typeId;

    @SerializedName("name")
    String name;

    @SerializedName("address")
    String address;

    @SerializedName("latitude")
    double latitude;

    @SerializedName("longitude")
    double longitude;

    @SerializedName("website")
    String website;

    @SerializedName("image")
    String image;

    @SerializedName("created_at")
    String createdAt;

    @SerializedName("updated_at")
    String updatedAt;

    @SerializedName("google_string")
    String googleString;

    @SerializedName("display_string")
    String displayString;

}
