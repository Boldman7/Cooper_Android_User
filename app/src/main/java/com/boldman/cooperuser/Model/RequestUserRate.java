package com.boldman.cooperuser.Model;

public class RequestUserRate {

    private String user_time;
    private String server_time;

    public void setUserTime(String sUserTime){ user_time = sUserTime; }
    public void setsServerTime(String sServerTime){ server_time = sServerTime; }

    public String getUserTime() { return user_time;}
    public String getServerTime() { return server_time;}

}
