package com.boldman.cooperuser.Model;

public class RequestUserPay {
    private float pay_amount;
    private String user_time;
    private String server_time;

    public void setPayAmount(float sPayAmount){ pay_amount = sPayAmount; }
    public void setUserTime(String sUserTime){ user_time = sUserTime; }
    public void setsServerTime(String sServerTime){ server_time = sServerTime; }

    public float getPayAmount() { return pay_amount;}
    public String getUserTime() { return user_time;}
    public String getServerTime() { return server_time;}

}
