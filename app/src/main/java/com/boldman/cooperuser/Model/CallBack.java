package com.boldman.cooperuser.Model;

public class CallBack {
    private String type;
    private String event;
    private String msg;
    private String current_ride;
    private String server_time;

    public void setType(String sType){ type = sType;}
    public void setEvent(String sEvent) { event = sEvent;}
    public void setPayAmount(String sMsg){ msg = sMsg; }
    public void setCurrentRide(String sCurrentRide){ current_ride = sCurrentRide; }
    public void setsServerTime(String sServerTime){ server_time = sServerTime; }

    public String getType() { return type;}
    public String getEvent() { return event;}
    public String getMsg() { return msg;}
    public String getCurrentRide() { return current_ride;}
    public String getServerTime() { return server_time;}

}
