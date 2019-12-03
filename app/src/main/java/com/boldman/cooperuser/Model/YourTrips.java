package com.boldman.cooperuser.Model;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YourTrips implements Serializable {

    private int id;
    private int user_id;
    private int service_type;
    private double s_lat;
    private double s_lon;
    private double d_lat;
    private double d_lon;
    private String ride_msg;
    private String request_at;
    private int status;
    private int driver_id;
    private String ride_code;
    private String accept_at;
    private String arrive_at;
    private String book_at;
    private String start_at;
    private String user_end_at;
    private String driver_end_at;
    private String payment_method;
    private String pay_at;
    private String paid_at;
    private String user_rated;
    private float user_rating;
    private String user_rate_at;
    private String driver_rated;
    private float driver_rating;
    private String finish_at;
    private float distance;
    private float pay_amount;
    private String canceled_at;
    private String created_at;
    private String updated_at;
    private double rs_lat;
    private double rs_lon;
    private double rd_lat;
    private double rd_lon;
    private int base_fee;
    private String email;
    private int gender;
    private String firstname;
    private String lastname;
    private String phone_number;
    private String avatar;
    private double driver_avg_rating;
}
