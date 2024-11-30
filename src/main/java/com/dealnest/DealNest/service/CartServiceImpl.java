package com.dealnest.DealNest.service;

import com.dealnest.DealNest.entity.Cart;
import com.dealnest.DealNest.entity.Product;
import com.dealnest.DealNest.entity.User;
import com.dealnest.DealNest.repository.CartRepository;
import com.dealnest.DealNest.repository.ProductRepository;
import com.dealnest.DealNest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        User user = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();
        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);
        Cart cart = new Cart();
        if(ObjectUtils.isEmpty(cartStatus)) {
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart.setTotalPrice(product.getDiscountedPrice());
        }else {
            cart=cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountedPrice());
            return cartRepository.save(cart);
        }
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getCartByUserId(Integer userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        Double totalAmount = 0.0;
        List<Cart> updatedCart = new ArrayList<>();
        for (Cart cart : carts)
        {
            double totalPrice = cart.getProduct().getDiscountedPrice()*cart.getQuantity();
            cart.setTotalPrice(totalPrice);
            double mrp = cart.getProduct().getPrice()*cart.getQuantity();
            cart.setMrp(mrp);
            totalAmount += totalPrice;
            cart.setTotalAmount(totalAmount);
            updatedCart.add(cart);
        }
        return updatedCart;
    }

    @Override
    public Integer getCountOfProducts(Integer userId) {

        return cartRepository.countByUserId(userId);
    }

    @Override
    public void updateQuantity(String sy, Integer cartId) {
        Cart cart = cartRepository.findById(cartId).get();
        int updatedQuantity;
        if (sy.equalsIgnoreCase("de")){
            updatedQuantity=cart.getQuantity()-1;
            if(updatedQuantity<=0){
                cartRepository.deleteById(cartId);
            }else {
                cart.setQuantity(updatedQuantity);
                cartRepository.save(cart);
            }
        }else {
            updatedQuantity=cart.getQuantity()+1;
            cart.setQuantity(updatedQuantity);
            cartRepository.save(cart);
        }
    }
    public Cart getCartByUserIdAndProductId(Integer userId, Integer productId) {
        return cartRepository.findByUserIdAndProductId(userId, productId);
    }

}
