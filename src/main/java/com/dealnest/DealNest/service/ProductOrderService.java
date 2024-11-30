package com.dealnest.DealNest.service;

import com.dealnest.DealNest.entity.OrderRequest;
import com.dealnest.DealNest.entity.Product;
import com.dealnest.DealNest.entity.ProductOrder;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public interface ProductOrderService {
    ProductOrder saveOrder(int userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException;

    ProductOrder getOrderById(int id);

    ProductOrder getOrderByOrderId(String orderId);

    List<ProductOrder> getOrderByUserId(int userId);

    ProductOrder updateOrderStatus(int id, String status);

    List<ProductOrder> getAllOrders();

    List<ProductOrder> searchProductOrder(String ch);

    Page<ProductOrder> getAllOrdersPage(Integer pageNo, Integer pageSize);
}
