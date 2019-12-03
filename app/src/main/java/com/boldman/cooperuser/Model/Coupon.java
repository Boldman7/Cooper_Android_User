package com.boldman.cooperuser.Model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Coupon Model
 * Created by BoldMan
 * 2019.06.05
 */

public class Coupon implements Serializable {

    @Getter
    @Setter
    private long id;
    @Getter
    @Setter
    private String coupon_code;
    @Getter
    @Setter
    private Integer discount;
    @Getter
    @Setter
    private String discount_type;
    @Getter
    @Setter
    private String expiration;
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private Integer user_id;
    @Getter
    @Setter
    private String added_at;
    @Getter
    @Setter
    private String used_at;
    @Getter
    @Setter
    private String expired_at;
    @Getter
    @Setter
    private String deleted_at;


}
