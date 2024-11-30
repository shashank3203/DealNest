package com.dealnest.DealNest.helper;

public enum OrderStatus {
    IN_PROGRESS(1, "In Progress"),
    ORDER_RECEIVED(2, "Order Received"),
    PRODUCT_PACKED(3, "Product Packed"),
    ORDER_SHIPPED(4, "Order Shipped"),
    OUT_FOR_DELIVERY(5, "Out For Delivery"),
    ORDER_DELIVERED(6, "Order Delivered"),
    ORDER_CANCELLED(7, "Order Cancelled"),
    ORDER_PLACED(8, "Order Placed");

    private int id;
    private String name;

    private OrderStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
