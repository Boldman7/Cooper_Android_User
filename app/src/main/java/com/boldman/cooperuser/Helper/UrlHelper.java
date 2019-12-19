package com.boldman.cooperuser.Helper;

public class UrlHelper {

    public static final String map_address_url = "https://maps.googleapis.com/maps/api/";

    public static final String GOOGLE_NEARBY_SEARCH(String location,String radius,String type,String key){
        return "https://maps.googleapis.com/maps/api/place/textsearch/json?"+"&query="+type+"&location="+location+"&radius="+radius+"&key="+key;
    }

    public static final String GOOGLE_GET_PLACE_PHOTO(int maxwidth,String photoreference,String key){
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth="+maxwidth+"&photoreference="+photoreference+"&key="+key;
    }

    public static final String GOOGLE_GET_PLACE_DETAIL(String key,String placeid){
        return "https://maps.googleapis.com/maps/api/place/details/json?key="+key+"&placeid="+placeid;
    }

}
