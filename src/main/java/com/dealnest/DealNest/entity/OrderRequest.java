package com.dealnest.DealNest.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderRequest {

    private String name;

    private String email;

    private String mobileNumber;

    private String alternate;

    private String address;

    private String pinCode;

    private String city;

    private String state;

    private String landmark;

    private String paymentMethod;
}
