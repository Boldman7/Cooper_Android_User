package com.boldman.cooperuser.Api;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * ApiInterface Class
 * created by Boldman
 * 2019.05.10
 */

public interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("user/signup")
    Call<ResponseBody> doSignUp( @Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/verify")
    Call<ResponseBody> doSignVerify( @Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/signin")
    Call<ResponseBody> doSignIn( @Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/resignup")
    Call<ResponseBody> doReSignUp( @Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/forgot_password")
    Call<ResponseBody> doForgotPassword( @Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/reset_password")
    Call<ResponseBody> doResetPassword( @Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/get_service_type")
    Call<ResponseBody> doGetServiceType();

    @Headers("Content-Type: application/json")
    @POST("user/get_card")
    Call<ResponseBody> doGetCardList(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/add_card")
    Call<ResponseBody> doAddCard(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/delete_card")
    Call<ResponseBody> doDeleteCard(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/finished_rides")
    Call<ResponseBody> doFinishedRides(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/change_password")
    Call<ResponseBody> doChangePassword(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/get_profile")
    Call<ResponseBody> doGetProfile(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/update_profile")
    Call<ResponseBody> doUpdateProfile(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/social_signin")
    Call<ResponseBody> doSocialSignIn(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/get_favourite_location")
    Call<ResponseBody> doGetFavouriteLocation(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/delete_favourite_location")
    Call<ResponseBody> doDeleteFavouriteLocation(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/update_favourite_location")
    Call<ResponseBody> doUpdateFavouriteLocation(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/request_schedule_ride")
    Call<ResponseBody> doRequestScheduleRide(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/get_balance")
    Call<ResponseBody> doGetWalletBalance(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/add_money")
    Call<ResponseBody> doAddMoney(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/get_braintree_token")
    Call<ResponseBody> doGetBTToken(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/charge")
    Call<ResponseBody> doSendNonce(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/add_coupon")
    Call<ResponseBody> doAddCoupon(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/get_coupon")
    Call<ResponseBody> doGetCoupon(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/estimated_fair")
    Call<ResponseBody> doGetEstimatedFair(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/pay")
    Call<ResponseBody> doPay(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/search_place")
    Call<ResponseBody> doSearchLocalPlace(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/current_ride")
    Call<ResponseBody> doGetCurrentRide(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/booking_rides")
    Call<ResponseBody> doBookingRides(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/sos_email")
    Call<ResponseBody> doSendSOSEmail(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/cancel_ride")
    Call<ResponseBody> doCancelRide(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("user/update_quickblox")
    Call<ResponseBody> doUpdateQuickBlox(@Body JsonObject body);
//    @Headers("Content-Type: application/json")
//    @Multipart
//    @POST("user/update_profile")
//    Call<ResponseBody> doUpdateProfile(@PartMap Map<String, RequestBody> map);


    @GET("json?")
    Call<ResponseBody> getGoogleMapResponse(@Query("latlng") String param1, @Query("key") String param2);

}
