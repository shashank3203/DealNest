package com.dealnest.DealNest.repository;

import com.dealnest.DealNest.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    public Boolean existsByName(String name);

    List<Category> findAllByIsActiveTrue();
}
