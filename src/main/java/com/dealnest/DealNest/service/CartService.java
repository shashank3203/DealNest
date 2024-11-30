package com.dealnest.DealNest.service;

import com.dealnest.DealNest.entity.Cart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    Cart saveCart(Integer productId, Integer userId);

    List<Cart> getCartByUserId(Integer userId);

    Integer getCountOfProducts(Integer userId);

    void updateQuantity(String sy, Integer cartId);

    Cart getCartByUserIdAndProductId(Integer userId, Integer productId);
}
