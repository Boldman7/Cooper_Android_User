package com.boldman.cooperuser.Model;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public class AccessDetails {

//    Make it "true" for demo. For installation/customization make it as "false"
    public static boolean demo_build = false;

    //public static final String access_login = "http://34.204.43.32/api/login"; //Live Server
    public static final String access_login = "http://192.168.0.109:8084/api/login"; //Live Server
    //public static final String access_login = "http://192.168.2.10:8000/public/api/login"; //Local Server

    //  Demo purpose leave it as empty
    @SerializedName("username")
    @Expose
    public static String username = "";
    @SerializedName("password")
    @Expose
    public static String password;

//    Tranxit Installation/Customization
//    @SerializedName("username")
//    @Expose
//    public static String username = "schedule";
//
//    @SerializedName("password")
//    @Expose
//    public static String password = "123451";

    @SerializedName("status")
    @Expose
    public static Boolean status;

    @SerializedName("id")
    @Expose
    public static Integer id;
    @SerializedName("client_name")
    @Expose
    public static String clientName;
    @SerializedName("email")
    @Expose
    public static String email;
    @SerializedName("product")
    @Expose
    public static String product;

    @SerializedName("passport")
    @Expose
    public static String passport = "zpTsmHxa4A5BLHN02WvpiD0CSkUBnoSaBZtj5FMf";
    @SerializedName("clientid")
    @Expose
    public static Integer clientid = 6;
    @SerializedName("serviceurl")
    @Expose
    public static String serviceurl = "http://192.168.0.109:8084/";
    //public static String serviceurl = "http://34.204.43.32/";
    //public static String serviceurl = "http://192.168.2.10:8000/";
    @SerializedName("is_active")
    @Expose
    public static Integer isActive;
    @SerializedName("created_at")
    @Expose
    public static String createdAt;
    @SerializedName("updated_at")
    @Expose
    public static String updatedAt;

    @SerializedName("is_paid")
    @Expose
    public static int isPaid;
    @SerializedName("is_valid")
    @Expose
    public static int isValid;
    @SerializedName("site_title")
    @Expose
    public static String siteTitle;
    @SerializedName("site_logo")
    @Expose
    public static String siteLogo;
    @SerializedName("site_email_logo")
    @Expose
    public static String siteEmailLogo;
    @SerializedName("site_icon")
    @Expose
    public static String siteIcon;
    @SerializedName("site_copyright")
    @Expose
    public static String siteCopyright;
    @SerializedName("provider_select_timeout")
    @Expose
    public static String providerSelectTimeout;
    @SerializedName("provider_search_radius")
    @Expose
    public static String providerSearchRadius;
    @SerializedName("base_price")
    @Expose
    public static String basePrice;
    @SerializedName("price_per_minute")
    @Expose
    public static String pricePerMinute;
    @SerializedName("tax_percentage")
    @Expose
    public static String taxPercentage;
    @SerializedName("stripe_secret_key")
    @Expose
    public static String stripeSecretKey="sk_test_L5kPPqlaDqKyM5eMboXrBAno";
    @SerializedName("stripe_publishable_key")
    @Expose
    public static String stripePublishableKey="pk_test_9IoCaC1bKd9DUQ6x3E0YOTXa";
    @SerializedName("CASH")
    @Expose
    public static String cash;
    @SerializedName("CARD")
    @Expose
    public static String card;
    @SerializedName("manual_request")
    @Expose
    public static String manualRequest;
    @SerializedName("default_lang")
    @Expose
    public static String defaultLang;
    @SerializedName("currency")
    @Expose
    public static String currency;
    @SerializedName("distance")
    @Expose
    public static String distance;
    @SerializedName("scheduled_cancel_time_exceed")
    @Expose
    public static String scheduledCancelTimeExceed;
    @SerializedName("price_per_kilometer")
    @Expose
    public static String pricePerKilometer;
    @SerializedName("commission_percentage")
    @Expose
    public static String commissionPercentage;
    @SerializedName("store_link_android")
    @Expose
    public static String storeLinkAndroid;
    @SerializedName("store_link_ios")
    @Expose
    public static String storeLinkIos;
    @SerializedName("daily_target")
    @Expose
    public static String dailyTarget;
    @SerializedName("surge_percentage")
    @Expose
    public static String surgePercentage;
    @SerializedName("surge_trigger")
    @Expose
    public static String surgeTrigger;
    @SerializedName("demo_mode")
    @Expose
    public static String demoMode;
    @SerializedName("booking_prefix")
    @Expose
    public static String bookingPrefix;
    @SerializedName("sos_number")
    @Expose
    public static String sosNumber;
    @SerializedName("contact_number")
    @Expose
    public static String contactNumber;
    @SerializedName("contact_email")
    @Expose
    public static String contactEmail;
    @SerializedName("social_login")
    @Expose
    public static String socialLogin;

    @SerializedName("site_logo")
    @Expose
    public static Drawable site_icon;

}