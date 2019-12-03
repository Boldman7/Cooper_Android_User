package com.boldman.cooperuser.Model;

import java.io.Serializable;

public class YourTripServiceType implements Serializable {
    private Long id;
    private String name;
    private String provider_name;
    private String image;
    private Integer capacity;
    private Integer fixed;
    private Integer price;
    private Integer minute;
    private Integer hour;
    private Integer distance;
    private String calculator;
    private String description;
    private int status;

    public YourTripServiceType(){}

    public YourTripServiceType(Long id, String name, String provider_name, String image, Integer capacity, Integer fixed, Integer price, Integer minute, Integer hour,
                               Integer distance, String calculator, String description, int status){
        this.id = id;
        this.name = name;
        this.provider_name = provider_name;
        this.image = image;
        this.capacity = capacity;
        this.fixed = fixed;
        this.price = price;
        this.minute = minute;
        this.hour = hour;
        this.distance = distance;
        this.calculator = calculator;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getFixed() {
        return fixed;
    }

    public void setFixed(Integer fixed) {
        this.fixed = fixed;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getCalculator() {
        return calculator;
    }

    public void setCalculator(String calculator) {
        this.calculator = calculator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
