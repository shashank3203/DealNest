package com.dealnest.DealNest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String orderId;

    private LocalDate orderDate;

    @ManyToOne
    private Product products;

    @ManyToOne
    private User user;

    private double price;

    private Integer quantity;

    private String status;

    private String paymentMethod;

    private String deliveryAddress;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;
}
