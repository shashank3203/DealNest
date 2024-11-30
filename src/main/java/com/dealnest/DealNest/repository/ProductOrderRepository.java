package com.dealnest.DealNest.repository;

import com.dealnest.DealNest.entity.ProductOrder;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
    ProductOrder findByOrderId(String orderId);

    List<ProductOrder> findOrderByUserId(int userId);

    List<ProductOrder> findByOrderIdContainingIgnoreCase(String ch);
}
