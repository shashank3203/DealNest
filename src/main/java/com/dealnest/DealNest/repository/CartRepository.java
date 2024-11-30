package com.dealnest.DealNest.repository;

import com.dealnest.DealNest.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Cart findByProductIdAndUserId(Integer userId, Integer productId);

    Integer countByUserId(Integer userId);

    List<Cart> findByUserId(Integer userId);

    Cart findByUserIdAndProductId(Integer userId, Integer productId);
}
