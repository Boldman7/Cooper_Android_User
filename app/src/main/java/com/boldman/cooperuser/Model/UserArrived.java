package com.boldman.cooperuser.Model;

public class UserArrived {

    private String api_token;
    private float latitude;
    private float longitude;
    private String driver_time;
    private String timestamp;

    public void setApiToken(String sApiToken) { api_token = sApiToken;}
    public void setLatitude(float sLatitude){ latitude = sLatitude; }
    public void setLongitude(float sLongitude){ longitude = sLongitude; }
    public void setDriverTime(String sDriverTime){ driver_time = sDriverTime; }
    public void setTimestamp(String sTimestamp) { timestamp = sTimestamp;}

    public String getApiToken() { return api_token;}
    public float getLatitude() { return latitude;}
    public float getLongitude() { return longitude;}
    public String getDriver_time() { return driver_time;}
    public String getTimestamp() { return timestamp;}

}
