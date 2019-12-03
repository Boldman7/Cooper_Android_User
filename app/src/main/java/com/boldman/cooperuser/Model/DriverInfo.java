package com.boldman.cooperuser.Model;

import java.util.List;

public class DriverInfo {

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
    private String driver_time;
    private float angle;
    private float avg_rating;
    private List<CarImage> carImageList;

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
    public void setDriverTime(String sDriverTime){ driver_time = sDriverTime; }
    public void setAngle(float sAngle){ angle = sAngle;}
    public void setAvgRating(float sAvgRating) { avg_rating = sAvgRating;}
    public void setCarImageList(List<CarImage> sCarImageList) { carImageList = sCarImageList;}

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
    public String getDriver_time() { return driver_time;}
    public float getAngle() { return angle;}
    public float getAvgRating() { return avg_rating;}
    public List<CarImage> getCarImageList() { return carImageList; }
}
