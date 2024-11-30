package com.dealnest.DealNest.controller;

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

import com.dealnest.DealNest.entity.*;
import com.dealnest.DealNest.helper.CommonUtils;
import com.dealnest.DealNest.helper.OrderStatus;
import com.dealnest.DealNest.service.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/dealnest/admin")
public class AdminController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductOrderService orderService;

    @Autowired
    private CommonUtils commonUtils;

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

    @GetMapping("/")
    public String dashboard(){
        return "admin/dashboard";
    }

    @GetMapping("/add-product")
    public String addProduct(Model model){
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/add-product";
    }

    @GetMapping("/add-category")
    public String addCategory(Model model){

        model.addAttribute("category", categoryService.getAllCategories());
        return "admin/add-category";
    }

    @PostMapping("/save-category")
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session)throws IOException {
        String fileName = file!= null ? file.getOriginalFilename() : "default.png";
        category.setImageName(fileName);
        Boolean existCategory = categoryService.existCategory(category.getName());

        if (existCategory) {
            session.setAttribute("errorMessage", "Category name already exists!");
        }else {
            Category savedCategory = categoryService.saveCategory(category);
            if (ObjectUtils.isEmpty(savedCategory)) {
                session.setAttribute("errorMessage", "Failed to save category!");
            }else {
                File saveFile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category" + File.separator
                        + file.getOriginalFilename());

                // System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                session.setAttribute("successMessage", "Category saved successfully!");
            }
        }
        return "redirect:/dealnest/admin/add-category";
    }

    @GetMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session){
        Boolean deleteCategory = categoryService.deleteCategory(id);

        if (deleteCategory){
            session.setAttribute("successDeleteMessage", "Category deleted successfully!");
        }else {
            session.setAttribute("errorDeleteMessage", "Failed to delete category!");
        }
        return "redirect:/dealnest/admin/add-category";
    }

    @GetMapping("/edit-category/{id}")
    public String editCategory(@PathVariable int id, Model model){
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        return "admin/edit-category";
    }

    @PostMapping("/update-category")
    public String updateCategory(@ModelAttribute Category category,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session) throws IOException {

        Category oldCategory = categoryService.getCategoryById(category.getId());

        if (!ObjectUtils.isEmpty(category)){
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename());
        }

        Category updatedCategory = categoryService.saveCategory(oldCategory);

       if (!ObjectUtils.isEmpty(updatedCategory)){
           session.setAttribute("successMessage", "Category updated successfully!");
       }else {
           session.setAttribute("errorMessage", "Failed to update category!");
       }
        return "redirect:/dealnest/admin/edit-category/"+ category.getId();
    }

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute Product product,
                              HttpSession session,
                              @RequestParam("file") MultipartFile file)
            throws IOException
    {

        String fileName = file.isEmpty() ? "default.png" : file.getOriginalFilename();
        product.setImageName(fileName);
        product.setDiscount(0);
        product.setDiscountedPrice(product.getPrice());

        Product savedProduct = productService.saveProduct(product);
        if (!ObjectUtils.isEmpty(savedProduct)){
            File saveFile = new ClassPathResource("static/images").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product" + File.separator
                    + file.getOriginalFilename());

            // System.out.println(path);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("successMessage", "Product saved successfully!");
        }else {
            session.setAttribute("errorMessage", "Failed to save product!");
        }
        return"redirect:/dealnest/admin/add-product";
    }

    @GetMapping("/view-products")
    public String viewProducts(Model model){

        model.addAttribute("products", productService.getAllProducts());
        return "admin/view-products";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session){
        Boolean deleteProduct = productService.deleteProduct(id);

        if (deleteProduct){
            session.setAttribute("successDeleteMessage", "Category deleted successfully!");
        }else {
            session.setAttribute("errorDeleteMessage", "Failed to delete category!");
        }
        return "redirect:/dealnest/admin/view-products";
    }

    @GetMapping("/edit-product/{id}")
    public String editProduct(@PathVariable int id, Model model){
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/edit-product";
    }

    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute Product product,
                                 @RequestParam("file") MultipartFile file,
                                 HttpSession session) throws IOException {

        Product oldProduct = productService.getProductById(product.getId());

        if (!ObjectUtils.isEmpty(product)){
            oldProduct.setTitle(product.getTitle());
            oldProduct.setDescription(product.getDescription());// is active is a get method in Category entity mentioned here.
            oldProduct.setImageName(file.isEmpty() ? oldProduct.getImageName() : file.getOriginalFilename());
            oldProduct.setCategory(product.getCategory());
            oldProduct.setPrice(product.getPrice());
            oldProduct.setIsActive(product.getIsActive());
            oldProduct.setDiscount(product.getDiscount());

            // Logic for Discounted Price
            Double discountPrice = product.getPrice() - (product.getPrice() * product.getDiscount() / 100);
            oldProduct.setDiscountedPrice(discountPrice);
            oldProduct.setStock(product.getStock());
        }

        Product updatedProduct = productService.saveProduct(oldProduct);

        if(product.getDiscount()<0 && product.getDiscount()>100){
            session.setAttribute("errorMessage", "Discount should be between 0 and 100!");
        }else {

            if (!ObjectUtils.isEmpty(updatedProduct)) {
                session.setAttribute("successMessage", "Product updated successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to update product!");
            }
        }
        return "redirect:/dealnest/admin/edit-product/"+ product.getId();
    }

    @GetMapping("/web-users")
    public String userList(Model model){
        model.addAttribute("users", userService.getAllUsers("ROLE_USER"));
        model.addAttribute("admin", userService.getAllUsers("ROLE_ADMIN"));
        return "admin/users";
    }

    @GetMapping("/update-status")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session){

        Boolean updateStatus = userService.updateUserAccountStatus(id, status);

        if (updateStatus){
            session.setAttribute("successMessage", "User account status updated successfully!");
        }else {
            session.setAttribute("errorMessage", "Failed to update user account status!");
        }

        return "redirect:/dealnest/admin/web-users";
    }

    @GetMapping("/view-orders")
    public String viewOrders(Model model, @RequestParam (name = "pageNo", defaultValue="0") Integer pageNo,
                             @RequestParam (name = "pageSize", defaultValue = "9") Integer pageSize){
        Page<ProductOrder> orders = orderService.getAllOrdersPage(pageNo, pageSize);
        if (orders != null) {
            List<ProductOrder> reversedOrders = new ArrayList<>(orders.getContent());
            Collections.reverse(reversedOrders);
            model.addAttribute("orders", reversedOrders);  // Use reversed list in the model
        }
        model.addAttribute("pageNo", orders.getNumber());
        model.addAttribute("pageSize", orders.getSize());
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("totalElements", orders.getTotalElements());
        model.addAttribute("isFirst", orders.isFirst());
        model.addAttribute("isLast", orders.isLast());
        return "admin/view-orders";
    }

    @GetMapping("/order-information")
    public String orderInformation(@ModelAttribute OrderRequest orderRequest, @RequestParam String orderId, Principal principal, Model model){

        ProductOrder order = orderService.getOrderByOrderId(orderId);

        //            model.addAttribute("orderRequest", orderAddressService.getOrderAddressByOrderId(order.getId()));
        model.addAttribute("address", order.getOrderAddress().getAddress()+", "+order.getOrderAddress().getCity()+", "+order.getOrderAddress().getState()+", "+order.getOrderAddress().getLandmark()+", "+order.getOrderAddress().getPinCode());
        model.addAttribute("orders", order);
        return "admin/order-detailed-information";
    }
    @PostMapping("/update-order-status")
    public String updateStatus(@RequestParam int id,@RequestParam int st, Principal principal, Model model, HttpSession session) throws MessagingException, UnsupportedEncodingException {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderStatus : values){
            if(orderStatus.getId() == st){
                status = orderStatus.getName();
            }
        }
        ProductOrder updateOrder = orderService.updateOrderStatus(id,status);
        commonUtils.sendMailForOrderStatusChange(updateOrder);
        if(!ObjectUtils.isEmpty(updateOrder)){
            session.setAttribute("successMessage", "Order Status updated successfully");
        }else {
            session.setAttribute("errorMessage", "Something Went Wrong");
        }
        return "redirect:/dealnest/admin/order-cancelled?id=" + id;
    }
    @GetMapping("/order-cancelled")
    public String orderCancelled(@RequestParam int id, Principal principal, Model model){
        ProductOrder order = orderService.getOrderById(id);

        //            model.addAttribute("orderRequest", orderAddressService.getOrderAddressByOrderId(order.getId()));
        model.addAttribute("address", order.getOrderAddress().getAddress()+", "+order.getOrderAddress().getCity()+", "+order.getOrderAddress().getState()+", "+order.getOrderAddress().getLandmark()+", "+order.getOrderAddress().getPinCode());
        model.addAttribute("name", order.getOrderAddress().getName());
        model.addAttribute("email", order.getOrderAddress().getEmail());
        model.addAttribute("mobileNumber", order.getOrderAddress().getMobileNumber());
        model.addAttribute("orders", order);
        return "admin/order-detailed-information";
    }

    @GetMapping("/search")
    public String search(@RequestParam String search, Model model){
        List<ProductOrder> orders = orderService.searchProductOrder(search);
        model.addAttribute("orders", orders);
        return "admin/view-orders";
    }

    @GetMapping("/search-product")
    public String searchProduct(@RequestParam String search, Model model){
        List<Product> products = productService.searchProduct(search);
        model.addAttribute("products", products);
        return "admin/view-products";
    }

    @GetMapping("/search-user")
    public String searchUser(@RequestParam String search, Model model){
        List<User> user = userService.searchUser(search);
        model.addAttribute("users", user);
        return "admin/users";
    }

    @GetMapping("/add-admin")
    public String addAdmin(){
        return "admin/add-admin";
    }

    @PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute("admin") User user,HttpSession session, @RequestParam("file") MultipartFile file)throws IOException {
        String fileName = file.isEmpty() ? "default.png" : file.getOriginalFilename();
        user.setImageName(fileName);
        User savedUser = userService.saveAdmin(user);
        if (!ObjectUtils.isEmpty(savedUser)){
            File saveFile = new ClassPathResource("static/images").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "user" + File.separator
                    + file.getOriginalFilename());

            // System.out.println(path);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("successMessage", "User saved successfully!");
        }else {
            session.setAttribute("errorMessage", "Failed to save user!");
        }
        return "redirect:/dealnest/admin/add-admin";
    }
}
