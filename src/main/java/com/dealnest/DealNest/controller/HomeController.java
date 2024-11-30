package com.dealnest.DealNest.controller;

import com.dealnest.DealNest.entity.Category;
import com.dealnest.DealNest.entity.Product;
import com.dealnest.DealNest.entity.ProductOrder;
import com.dealnest.DealNest.entity.User;
import com.dealnest.DealNest.helper.CommonUtils;
import com.dealnest.DealNest.service.CartService;
import com.dealnest.DealNest.service.CategoryService;
import com.dealnest.DealNest.service.ProductService;
import com.dealnest.DealNest.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/dealnest")
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
    public String home(Model model, @RequestParam(value = "category", required = false) String category){

        List<Category> categories = categoryService.getAllActiveCategories();
        List<Product> products = productService.getAllActiveProducts(category);
        if (products != null) {
            Collections.reverse(products);
            model.addAttribute("orders", products);  // Use reversed list in the model
        }

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "home";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/products")
    public String products(Model model, @RequestParam(value = "category", required = false) String category){
        List<Category> categories = categoryService.getAllActiveCategories();
        List<Product> products = productService.getAllActiveProducts(category);

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("paramValue", category);
        return "products";
    }

    @GetMapping("/viewproduct/{id}")
    public String viewProducts(@PathVariable int id, Model model){

        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "view-product";
    }

    @PostMapping("/save-user")
    public String saveUser(@ModelAttribute User user,HttpSession session, @RequestParam("file") MultipartFile file)throws IOException {
        try {
            String fileName = file.isEmpty() ? "default.png" : file.getOriginalFilename();
            user.setImageName(fileName);
            User savedUser = userService.saveUser(user);
            if (!ObjectUtils.isEmpty(savedUser)) {
                File saveFile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "user" + File.separator
                        + file.getOriginalFilename());

                // System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                session.setAttribute("successMessage", "User saved successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to save user!");
                return "redirect:/dealnest/register";
            }
        }catch (RuntimeException e) {
            session.setAttribute("errorMessage", e.getMessage());
            return "redirect:/dealnest/register";
        }
        return "redirect:/dealnest/login";
    }

    @GetMapping("/forget-password")
    public String forgerPassword(){
        return "forget-password";
    }

    @PostMapping("/forget-password")
    public String processForgerPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        User user = userService.getUserByEmail(email);

        if (ObjectUtils.isEmpty(user)){
            session.setAttribute("errorMessage", "No user found with this email!");
            return "forget-password";
        }else {
            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            // Generate URL for email - http://localhost:8080/dealnest/reset-password?token=sdasdasxadfcascade

            String resetUrl = commonUtils.generateUrl(request)+"/dealnest/reset-password?token="+resetToken;

            Boolean sendEmail = commonUtils.sendEmail(resetUrl, email);
            if(sendEmail){
                session.setAttribute("successMessage", "Reset password link sent to your email!");
                return "forget-password";
            }else {
                session.setAttribute("errorMessage", "Failed to send reset password link!");
                return "forget-password";
            }
        }
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String token, HttpSession session, Model model){
        User user = userService.getUserByToken(token);
        if (user == null) {
            model.addAttribute("message", "Invalid or expired reset password link!");
            return "message";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token, @RequestParam String newPassword, HttpSession session, Model model){
        User user = userService.getUserByToken(token);
        if (user == null) {
            model.addAttribute("message", "Invalid or expired reset password link!");
            return "message";
        }else {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            userService.updateUser(user);
            model.addAttribute("message", "Password changed successfully!");
            return "message";
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam String search, Model model){
        List<Product> products = productService.searchProduct(search);
        model.addAttribute("products", products);
        List<Category> categories = categoryService.getAllActiveCategories();
        model.addAttribute("categories", categories);
        return "products";
    }
}
