package com.boldman.cooperuser.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.boldman.cooperuser.Model.ServiceType;
import com.google.gson.Gson;

import java.util.List;

/*
*   SharedHelper
*   Created by Boldman. 2019.05.09
* */

public class SharedHelper {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static void putKey(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.commit();

    }

    public static String getKey(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        String Value = sharedPreferences.getString(Key, "");
        return Value;

    }

    public static void clearSharedPreferences(Context context)
    {
        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

    public static void putServiceTypeList(Context context, String Key, List<ServiceType> object){

        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        editor.putString(Key, json);
        editor.commit();
    }

//    public static Object getObject(Context contextGetKey, String Key){
//        sharedPreferences = contextGetKey.getSharedPreferences("Cache", Context.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString(Key, "");
//        Object value = gson.fromJson(json, Object);
//        return value;
//    }

}