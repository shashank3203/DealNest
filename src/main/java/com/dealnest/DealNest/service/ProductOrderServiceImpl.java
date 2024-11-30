package com.dealnest.DealNest.service;

import com.dealnest.DealNest.entity.*;
import com.dealnest.DealNest.helper.CommonUtils;
import com.dealnest.DealNest.helper.OrderStatus;
import com.dealnest.DealNest.repository.CartRepository;
import com.dealnest.DealNest.repository.ProductOrderRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductOrderServiceImpl implements ProductOrderService{

    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CommonUtils commonUtils;
    @Override
    public ProductOrder saveOrder(int userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException {

        List<Cart> carts = cartRepository.findByUserId(userId);
        ProductOrder productOrder = new ProductOrder();
        for (Cart cart : carts){
            long random10DigitNumber = (long) (Math.random() * 9000000000L) + 1000000000L;
            productOrder.setOrderId("DNOD-"+random10DigitNumber);
            productOrder.setOrderDate(LocalDate.now());
            productOrder.setProducts(cart.getProduct());
            productOrder.setPrice(cart.getProduct().getDiscountedPrice() * cart.getQuantity());
            productOrder.setUser(cart.getUser());
            productOrder.setQuantity(cart.getQuantity());
            productOrder.setStatus(OrderStatus.IN_PROGRESS.name());
            productOrder.setPaymentMethod(orderRequest.getPaymentMethod());

            OrderAddress address = new OrderAddress();
            address.setName(orderRequest.getName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNumber(orderRequest.getMobileNumber());
            address.setAlternate(orderRequest.getAlternate());
            address.setAddress(orderRequest.getAddress());
            address.setPinCode(orderRequest.getPinCode());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setLandmark(orderRequest.getLandmark());

            productOrder.setOrderAddress(address);
            productOrder.setDeliveryAddress(address.getAddress()+", "+address.getCity()+", "+address.getState()+", "+address.getLandmark()+", "+address.getPinCode());
            cartRepository.delete(cart);
            ProductOrder savedOrder = productOrderRepository.save(productOrder);
            commonUtils.sendMailForOrder(savedOrder);
        }
        return productOrder;
    }

    @Override
    public ProductOrder getOrderById(int id) {
        return productOrderRepository.findById(id).orElse(null);
    }

    @Override
    public ProductOrder getOrderByOrderId(String orderId) {
        return productOrderRepository.findByOrderId(orderId);
    }

    @Override
    public List<ProductOrder> getOrderByUserId(int userId) {
        return productOrderRepository.findOrderByUserId(userId);
    }

    @Override
    public ProductOrder updateOrderStatus(int id, String status) {
        Optional<ProductOrder> order = productOrderRepository.findById(id);
        if (order.isPresent()){
            ProductOrder productOrder = order.get();
            productOrder.setStatus(status);
            return productOrderRepository.save(productOrder);
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return productOrderRepository.findAll();
    }

    @Override
    public List<ProductOrder> searchProductOrder(String ch) {
        return productOrderRepository.findByOrderIdContainingIgnoreCase(ch);
    }

    @Override
    public Page<ProductOrder> getAllOrdersPage(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProductOrder> allOrders = productOrderRepository.findAll(pageable);
        return allOrders;
    }
}
