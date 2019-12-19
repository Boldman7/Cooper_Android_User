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
    private String email;
    private int gender;
    private String first_name;
    private String last_name;
    private String phone_number;
    private String avatar;
    private double driver_avg_rating;
    private int service_type;
    private double s_lat;
    private double s_lon;
    private String s_address;
    private double d_lat;
    private double d_lon;
    private String d_address;
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
    private int coupon_id;
    private String pay_at;
    private String paid_at;
    private String user_rated;
    private String user_rating;
    private String user_rate_at;
    private String driver_rated;
    private String driver_rating;
    private String finish_at;
    private float distance;
    private float rental_distance;
    private float rental_hours;
    private float base_fee;
    private float distance_fee;
    private float tax_fee;
    private float cooper_fee;
    private float cooper_tax_fee;
    private float discount_fee;
    private float pay_amount;
    private float user_start_balance;
    private float user_end_balance;
    private float driver_start_balance;
    private float driver_end_balance;
    private String canceled_at;
    private String cancel_reason;
    private String cancel_by;
    private String created_at;
    private String updated_at;
    private double rs_lat;
    private double rs_lon;
    private String rs_address;
    private double rd_lat;
    private double rd_lon;
    private String rd_address;
    private double last_lat;
    private double last_lon;
    private String fc_at;
    private String username;
}
