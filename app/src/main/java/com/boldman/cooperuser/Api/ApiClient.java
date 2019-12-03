package com.boldman.cooperuser.Api;

import com.boldman.cooperuser.Helper.UrlHelper;
import com.boldman.cooperuser.Utils.GlobalConstants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.boldman.cooperuser.Utils.GlobalConstants.SERVER_HTTP_URL;

/**
 * ApiClient Class
 * created by Boldman
 * 2019.05.10
 */

public class ApiClient {

    static Retrofit retrofit = null;
    private static Retrofit retrofit_address = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        String server_api_url =
                SERVER_HTTP_URL + "/" +
                GlobalConstants.SERVER_API + "/";

        retrofit = new Retrofit.Builder()
                .baseUrl(server_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }


    public static Retrofit getGoogleMapClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if (retrofit_address==null) {
            retrofit_address = new Retrofit.Builder()
                    .baseUrl(UrlHelper.map_address_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit_address;
    }

}
