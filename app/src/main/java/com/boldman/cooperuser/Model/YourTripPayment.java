package com.boldman.cooperuser.Model;

import java.io.Serializable;

public class YourTripPayment implements Serializable {
    private Long id;
    private Long request_id;
    private Long promocode_id;
    private Long payment_id;
    private Long payment_mode;
    private Integer fixed;
    private Integer distance;
    private Integer commision;
    private Integer discount;
    private Integer tax;
    private Integer wallet;
    private Integer surge;
    private Integer total;
    private Double payable;
    private Integer provider_commission;
    private Integer provider_pay;
    
    public YourTripPayment(){ }

    public YourTripPayment(Long id, Long request_id, Long promocode_id, Long payment_id, Long payment_mode, Integer fixed, Integer distance, Integer commision, Integer discount, Integer
                           tax, Integer wallet, Integer surge, Integer total, Double payable, Integer provider_commission, Integer provider_pay){
        this.id = id;
        this.request_id = request_id;
        this.promocode_id = promocode_id;
        this.payment_id = payment_id;
        this.payment_mode = payment_mode;
        this.fixed = fixed;
        this.distance = distance;
        this.commision = commision;
        this.discount = discount;
        this.tax = tax;
        this.wallet = wallet;
        this.surge = surge;
        this.total = total;
        this.payable = payable;
        this.provider_commission = provider_commission;
        this.provider_pay = provider_pay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequest_id() {
        return request_id;
    }

    public void setRequest_id(Long request_id) {
        this.request_id = request_id;
    }

    public Long getPromocode_id() {
        return promocode_id;
    }

    public void setPromocode_id(Long promocode_id) {
        this.promocode_id = promocode_id;
    }

    public Long getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(Long payment_id) {
        this.payment_id = payment_id;
    }

    public Long getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(Long payment_mode) {
        this.payment_mode = payment_mode;
    }

    public Integer getFixed() {
        return fixed;
    }

    public void setFixed(Integer fixed) {
        this.fixed = fixed;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getCommision() {
        return commision;
    }

    public void setCommision(Integer commision) {
        this.commision = commision;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public Integer getWallet() {
        return wallet;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public Integer getSurge() {
        return surge;
    }

    public void setSurge(Integer surge) {
        this.surge = surge;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getPayable() {
        return payable;
    }

    public void setPayable(Double payable) {
        this.payable = payable;
    }

    public Integer getProvider_commission() {
        return provider_commission;
    }

    public void setProvider_commission(Integer provider_commission) {
        this.provider_commission = provider_commission;
    }

    public Integer getProvider_pay() {
        return provider_pay;
    }

    public void setProvider_pay(Integer provider_pay) {
        this.provider_pay = provider_pay;
    }
}
