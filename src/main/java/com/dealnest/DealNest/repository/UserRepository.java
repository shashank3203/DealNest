package com.dealnest.DealNest.repository;

import com.dealnest.DealNest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User getUserByEmail(String email);

    List<User> findByRole(String role);

    User getUserByResetToken(String token);

    List<User> findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(String ch, String ch1);

    boolean existsByEmail(String email);
}
