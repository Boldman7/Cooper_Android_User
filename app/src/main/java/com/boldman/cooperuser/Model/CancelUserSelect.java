package com.boldman.cooperuser.Model;

public class CancelUserSelect {

    private String email;
    private String user_time;
    private String server_time;

    public void setEmail(String sEmail){ email = sEmail; }
    public void setUserTime(String sUserTime) {user_time = sUserTime;}
    public void setServerTime(String sServerTime) {server_time = sServerTime;}

    public String getEmail() {return email;}
    public String getUserTime() {return user_time;}
    public String getServerTime() {return server_time;}

}
