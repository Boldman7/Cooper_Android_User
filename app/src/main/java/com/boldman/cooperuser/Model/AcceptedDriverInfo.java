package com.boldman.cooperuser.Model;

import java.util.List;

public class AcceptedDriverInfo {

    private String email;
    private int gender;
    private String firstname;
    private String lastname;
    private String phone_number;
    private double latitude;
    private double longitude;
    private String car_number;
    private String car_model;
    private String avatar;
    private String ride_code;
    private String driver_avg_rating;
    private String driver_time;
    private String server_time;
    private float angle;

    public void setEmail(String sEmail){ email = sEmail; }
    public void setGender(int sGender){ gender = sGender; }
    public void setFirstName(String sFirstName){ firstname = sFirstName; }
    public void setLastName(String sLastName) { lastname = sLastName;}
    public void setPhoneNumber(String sPhoneNumber){ phone_number = sPhoneNumber; }
    public void setLatitude(float sLatitude){ latitude = sLatitude; }
    public void setLongitude(float sLongitude){ longitude = sLongitude; }
    public void setCarNumber(String sCarNumber){ car_number = sCarNumber; }
    public void setCarModel(String sCarModel){ car_model = sCarModel; }
    public void setAvatar(String sAvatar){ avatar = sAvatar; }
    public void setRideCode(String sRideCode) { ride_code = sRideCode;}
    public void setDriverAvgRating(String sDriverAvgRating) { driver_avg_rating = sDriverAvgRating;}
    public void setDriverTime(String sDriverTime){ driver_time = sDriverTime; }
    public void setServerTime(String sServerTime) { server_time = sServerTime;}
    public void setAngle(float sAngle){ angle = sAngle;}

    public String getEmail() { return email;}
    public int getGender() { return gender;}
    public String getFirstName() { return firstname;}
    public String getLastName() { return lastname;}
    public String getPhoneNumber() { return phone_number;}
    public double getLatitude() { return latitude;}
    public double getLongitude() { return longitude;}
    public String getCarNumber() { return car_number;}
    public String getCarModel() { return car_model;}
    public String getAvatar() { return avatar;}
    public String getRideCode() { return ride_code;}
    public String getDriverAvgRating() { return driver_avg_rating;}
    public String getDriverTime() { return driver_time;}
    public String getServerTime() { return server_time;}
    public double getAngle() { return angle;}
}
