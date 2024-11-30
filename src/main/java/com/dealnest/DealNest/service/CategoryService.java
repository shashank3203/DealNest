package com.dealnest.DealNest.service;

import com.dealnest.DealNest.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    // Define your category service methods here
    Category saveCategory(Category category);

    Boolean existCategory(String name);

    List<Category> getAllCategories();

    Boolean deleteCategory(int id);

    Category getCategoryById(int id);

    List<Category> getAllActiveCategories();
}
