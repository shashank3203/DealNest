package com.dealnest.DealNest.controller;

import com.dealnest.DealNest.entity.*;
import com.dealnest.DealNest.helper.CommonUtils;
import com.dealnest.DealNest.helper.OrderStatus;
import com.dealnest.DealNest.service.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RequestMapping("/dealnest/user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductOrderService orderService;
    @Autowired
    private CommonUtils commonUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute
    public void getUserDetails(Principal principal, Model model){
        if (principal!=null){
            String email = principal.getName();
            User user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            model.addAttribute("count", cartService.getCountOfProducts(user.getId()));
        }
        List<Category> categories = categoryService.getAllActiveCategories();
        model.addAttribute("categories", categories);
    }

    @RequestMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/add-to-cart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session){
        Cart cart = cartService.saveCart(pid, uid);
        if(ObjectUtils.isEmpty(cart)){
            session.setAttribute("errorMessage", "Something Went Wrong");
        }else {
            session.setAttribute("successMessage", "Product added to cart successfully!");
        }
        return "redirect:/dealnest/viewproduct/"+pid;
    }

    @GetMapping("/cart")
    public String cart(Principal principal, Model model, HttpSession session){
        User user = getLoggedInUserDetails(principal);
        List<Cart> cart = cartService.getCartByUserId(user.getId());
        model.addAttribute("cart", cart);
        if(cart.size() > 0) {
            model.addAttribute("totalAmount", cart.get(cart.size() - 1).getTotalAmount());
        }
        return "user/cart";
    }

    private User getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();
        return userService.getUserByEmail(email);
    }

    @GetMapping("/update-quantity")
    public String updateQuantity(@RequestParam String sy, @RequestParam Integer cartId, HttpSession session){

        cartService.updateQuantity(sy, cartId);
        return "redirect:/dealnest/user/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Principal principal, Model model){
        User user = getLoggedInUserDetails(principal);
        List<Cart> cart = cartService.getCartByUserId(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        if(cart.size() > 0) {
            model.addAttribute("totalAmount", cart.get(cart.size() - 1).getTotalAmount()+40);
        }
        return "user/checkout";
    }

    @PostMapping("/orderedSuccessfully")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest, Principal principal, Model model) throws MessagingException, UnsupportedEncodingException {
        User user = getLoggedInUserDetails(principal);
        ProductOrder savedOrder = orderService.saveOrder(user.getId(), orderRequest);
        ProductOrder order = orderService.getOrderById(savedOrder.getId());

        model.addAttribute("order", order);
        return "user/ordered-successfully";
    }

    @GetMapping("/order-details")
    public String orderDetails(@ModelAttribute OrderRequest orderRequest,@RequestParam String orderId, Principal principal, Model model){

        User user = getLoggedInUserDetails(principal);
        List<Cart> cart = cartService.getCartByUserId(user.getId());
        ProductOrder order = orderService.getOrderByOrderId(orderId);

//            model.addAttribute("orderRequest", orderAddressService.getOrderAddressByOrderId(order.getId()));
        model.addAttribute("address", order.getOrderAddress().getAddress()+", "+order.getOrderAddress().getCity()+", "+order.getOrderAddress().getState()+", "+order.getOrderAddress().getLandmark()+", "+order.getOrderAddress().getPinCode());
        model.addAttribute("cart", cart);
        model.addAttribute("order", order);
        return "user/order-details";
    }
    @GetMapping("/orders")
    public String userOrders(Principal principal, Model model){
        User user = getLoggedInUserDetails(principal);

        List<ProductOrder> orders = orderService.getOrderByUserId(user.getId());
        if (orders != null) {
                // Reverse the list in the controller
            Collections.reverse(orders);
        }
        model.addAttribute("orders", orders);
        return "user/orders";
    }

    @GetMapping("/order-information")
    public String orderInformation(@ModelAttribute OrderRequest orderRequest,@RequestParam String orderId, Principal principal, Model model){

        User user = getLoggedInUserDetails(principal);
        ProductOrder order = orderService.getOrderByOrderId(orderId);

    //            model.addAttribute("orderRequest", orderAddressService.getOrderAddressByOrderId(order.getId()));
        model.addAttribute("address", order.getOrderAddress().getAddress()+", "+order.getOrderAddress().getCity()+", "+order.getOrderAddress().getState()+", "+order.getOrderAddress().getLandmark()+", "+order.getOrderAddress().getPinCode());
        model.addAttribute("orders", order);
        return "user/order-information";
    }

    @GetMapping("/update-status")
    public String updateStatus(@RequestParam int id,@RequestParam int st, Principal principal, Model model, HttpSession session) throws MessagingException, UnsupportedEncodingException {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderStatus : values){
            if(orderStatus.getId() == st){
                status = orderStatus.getName();
            }
            }
        ProductOrder updateOrder = orderService.updateOrderStatus(id,status);
        commonUtils.sendMailForOrderCancelled(updateOrder);
        if(!ObjectUtils.isEmpty(updateOrder)){
            session.setAttribute("successMessage", "Order Status updated successfully");
        }else {
            session.setAttribute("errorMessage", "Something Went Wrong");
        }

        User user = getLoggedInUserDetails(principal);
        ProductOrder order = orderService.getOrderById(id);
        return "redirect:/dealnest/user/order-cancelled?id=" + id;
    }

    @GetMapping("/order-cancelled")
    public String orderCancelled(@RequestParam int id, Principal principal, Model model){
        User user = getLoggedInUserDetails(principal);
        ProductOrder order = orderService.getOrderById(id);

            //            model.addAttribute("orderRequest", orderAddressService.getOrderAddressByOrderId(order.getId()));
        model.addAttribute("address", order.getOrderAddress().getAddress()+", "+order.getOrderAddress().getCity()+", "+order.getOrderAddress().getState()+", "+order.getOrderAddress().getLandmark()+", "+order.getOrderAddress().getPinCode());
        model.addAttribute("name", order.getOrderAddress().getName());
        model.addAttribute("email", order.getOrderAddress().getEmail());
        model.addAttribute("mobileNumber", order.getOrderAddress().getMobileNumber());
        model.addAttribute("orders", order);
        return "user/order-information";
    }

    @GetMapping("/profile")
    public String profile(){
//        User user = userService.getUserByEmail(email);
        return "user/profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile(){
    //        User user = userService.getUserByEmail(email);
        return "user/edit-profile";
    }
    @GetMapping("/change-password")
    public String changePassword(){
            //        User user = userService.getUserByEmail(email);
        return "user/change-password";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user, @RequestParam("file") MultipartFile imageName, HttpSession session) {

        User updateUserProfile = userService.updateUserProfile(user, imageName);
        if (ObjectUtils.isEmpty(updateUserProfile)) {
            session.setAttribute("errorMessage", "Something went wrong");
        } else {
            session.setAttribute("successMessage", "Profile Updated Successfully!");
        }
        return "redirect:/dealnest/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword, @RequestParam String newPassword, Principal principal, HttpSession session) {

        User user = getLoggedInUserDetails(principal);

        boolean matches = passwordEncoder.matches(currentPassword, user.getPassword());
        if (matches) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.updateUser(user);
            if (ObjectUtils.isEmpty(user)){
                session.setAttribute("errorMessage", "Something went wrong");
            }else {
                session.setAttribute("successMessage", "Password changed successfully!");
            }
        }else {
            session.setAttribute("errorMessage", "Please enter correct password!");
        }
        return "redirect:/dealnest/user/profile";
    }
}