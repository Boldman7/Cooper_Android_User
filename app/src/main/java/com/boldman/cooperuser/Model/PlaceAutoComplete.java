package com.boldman.cooperuser.Model;


import java.io.Serializable;

/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public class PlaceAutoComplete implements Serializable {

    private String place_id;
    private String description;

    public String getPlaceDesc() {
        return description;
    }

    public void setPlaceDesc(String placeDesc) {
        description = placeDesc;
    }

    public String getPlaceID() {
        return place_id;
    }

    public void setPlaceID(String placeID) {
        place_id = placeID;
    }

}
