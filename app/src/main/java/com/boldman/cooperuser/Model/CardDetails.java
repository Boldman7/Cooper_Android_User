package com.boldman.cooperuser.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * CardDetails Model
 * Created by BoldMan
 * 2019.05.26
 */


public class CardDetails {

    private int id;
    private int user_id;
    private String brand;
    private String card_no;
    private int expiry_month;
    private int expiry_year;
    private int is_default;
    private int cvc;
    private String last_four;

    public void setId(int id) { this.id = id; }
    public void setUserId(int user_id) { this.user_id = user_id;}
    public void setBrand(String brand) { this.brand = brand;}
    public void setCardNo(String card_no) { this.card_no = card_no;}
    public void setExpiryMonth(int expiry_month) { this.expiry_month = expiry_month;}
    public void setExpiryYear(int expiry_year) { this.expiry_year = expiry_year;}
    public void setIsDefalut(int is_default) { this.is_default = is_default;}
    public void setCvc(int cvc) { this.cvc = cvc;}

    public int getId() { return id;}
    public int getUserId() { return user_id;}
    public String getBrand() { return brand;}
    public String getCardNo() { return card_no;}
    public int getExpiryMonth() { return expiry_month;}
    public int getExpiryYear() { return expiry_year;}
    public int getIsDefault() { return is_default;}
    public int getCvc() { return cvc;}

    public String getLastFour() {

        if (card_no != null && card_no.length() > 4){

            return card_no.substring(card_no.length() - 4);

        }else{
            return "";
        }

    }

    public void setLastFour(String sLastFour) { last_four = sLastFour; }
}
