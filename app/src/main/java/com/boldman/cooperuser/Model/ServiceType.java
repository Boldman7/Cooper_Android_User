package com.boldman.cooperuser.Model;

public class ServiceType {

    private int id;
    private String name;
    private String driver_name;
    private String image;
    private String marker_url;
    private int capacity;
    private int fixed;
    private float price;
    private int minute;
    private int hour;
    private float distance;
    private String calculator;
    private String description;
    private int status;
    private String created_at;
    private String updated_at;

    public void setId(int sId){ id = sId; }
    public void setName(String sName){ name = sName; }
    public void setDriver_name(String sDriverName){ driver_name = sDriverName; }
    public void setImage(String sImage){ image = sImage; }
    public void setMarker_url(String sMarkerUrl){ marker_url = sMarkerUrl; }
    public void setCapacity(int sCapacity){ capacity = sCapacity; }
    public void setFixed(int sFixed){ fixed = sFixed; }
    public void setPrice(float sPrice){ price = sPrice; }
    public void setMinute(int sMinute){ minute = sMinute; }
    public void setHour(int sHour){ hour = sHour; }
    public void setDistance(Float sDistance){ distance= sDistance; }
    public void setCalculator(String sCalculator){ calculator = sCalculator; }
    public void setDescription(String sDescription){ description = sDescription; }
    public void setStatus(int sStatus){ status = sStatus; }
    public void setCreated_at(String sCreatedAt){ created_at = sCreatedAt; }
    public void setUpdated_at(String sUpdatedAt){ updated_at = sUpdatedAt; }

    public int getId(){ return id;}
    public String getName() { return name;}
    public String getDriverName() { return driver_name;}
    public String getImage() { return image;}
    public String getMarker_url() { return marker_url;}
    public int getCapacity() { return capacity;}
    public int getFixed() { return fixed;}
    public float getPrice() { return price;}
    public int getMinute() { return minute;}
    public int getHour() { return hour;}
    public float getDistance() { return distance;}
    public String getCalculator() { return calculator;}
    public String getDescription() { return description;}
    public int getStatus() { return status;}
    public String getCreated_at() { return created_at;}
    public String getUpdated_at() { return updated_at;}

}
