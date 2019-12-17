package com.boldman.cooperuser.Model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RequestUserPay implements Serializable {

    private float distance;
    private float duration;
    private float base_fee;
    private float distance_fee;
    private float tax_fee;
    private String user_time;
    private String server_time;
}
