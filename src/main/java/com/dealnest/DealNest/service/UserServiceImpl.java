package com.dealnest.DealNest.service;

import com.dealnest.DealNest.entity.Product;
import com.dealnest.DealNest.entity.User;
import com.dealnest.DealNest.helper.AppConstant;
import com.dealnest.DealNest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with this email already exists!, Please Login");
        }

        user.setRole("ROLE_USER"); // Set default role to USER for new users
        user.setIsEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password before saving
        return userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public List<User> getAllUsers(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Boolean updateUserAccountStatus(Integer id, Boolean status) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            User user = byId.get();
            user.setIsEnabled(status);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void countFailedAttempts(User user) {

        user.setFailedAttempts(user.getFailedAttempts() + 1);
        userRepository.save(user);

    }

    @Override
    public void userAccountLock(User user) {

        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public Boolean unlockAccountTimeExpired(User user) {
        long lockTime = user.getLockTime().getTime();
        long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
        long currentTime = System.currentTimeMillis();

        if (currentTime > unLockTime) {
            user.setFailedAttempts(0);
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(int id) {

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        User user = userRepository.findByEmail(email);
        user.setResetToken(resetToken);
        userRepository.save(user);
    }

    @Override
    public User getUserByToken(String token) {
        return userRepository.getUserByResetToken(token);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUserProfile(User user, MultipartFile image) {
        User userById = userRepository.findById(user.getId()).orElse(null);
        if (!image.isEmpty()) {
            userById.setImageName(image.getOriginalFilename());
        }
        if (userById != null) {
            userById.setName(user.getName());
            userById.setPhoneNumber(user.getPhoneNumber());
            userById.setAddress(user.getAddress());
            userById.setCity(user.getCity());
            userById.setState(user.getState());
            userById.setPinCode(user.getPinCode());
            userById = userRepository.save(userById);
        }
        try {
            if (!image.isEmpty()) {
                File saveFile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "user" + File.separator
                        + image.getOriginalFilename());

                // System.out.println(path);
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return userById;
    }
    @Override
    public List<User> searchUser(String ch) {
        return userRepository.findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(ch, ch);
    }

    @Override
    public User saveAdmin(User user) {
        user.setRole("ROLE_ADMIN"); // Set default role to USER for new users
        user.setIsEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password before saving
        return userRepository.save(user);
    }
}
