package com.boldman.cooperuser.Model;

public class StartRide {
    private float latitude;
    private float longitude;
    private String server_time;

    public void setLatitude(float sLatitude){ latitude = sLatitude; }
    public void setLongitude(float sLongitude){ longitude = sLongitude; }
    public void setServerTime(String sServerTime){ server_time = sServerTime; }

    public float getLatitude() { return latitude;}
    public float getLongitude() { return longitude;}
    public String getServerTime() { return server_time;}

}
