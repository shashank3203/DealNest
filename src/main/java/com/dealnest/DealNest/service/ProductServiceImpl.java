package com.dealnest.DealNest.service;

import com.dealnest.DealNest.entity.Category;
import com.dealnest.DealNest.entity.Product;
import com.dealnest.DealNest.entity.ProductOrder;
import com.dealnest.DealNest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);  // Saving the product to the database;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(int id) {
        Product product = productRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(product)) {
            productRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public Product getProductById(int id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }

    @Override
    public List<Product> getAllActiveProducts(String categoryName) {

        List<Product> products = null;
        if(ObjectUtils.isEmpty(categoryName)){
            products = productRepository.findByIsActiveTrue();  // Fetching all active products from the database;
        }else {
            products = productRepository.findByCategoryAndIsActiveTrue(categoryName);  // Fetching all active products from the database belonging to the specified category;
        }
        return products;
    }
    @Override
    public List<Product> searchProduct(String ch) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
    }
}
