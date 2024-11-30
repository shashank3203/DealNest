package com.dealnest.DealNest.service;

import com.dealnest.DealNest.entity.Product;
import com.dealnest.DealNest.entity.ProductOrder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    Product saveProduct(Product product);
    List<Product> getAllProducts();
    Boolean deleteProduct(int id);
    Product getProductById(int id);
    List<Product> getAllActiveProducts(String categoryName);
    List<Product> searchProduct(String ch);
}
