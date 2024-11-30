package com.dealnest.DealNest.service;

import com.dealnest.DealNest.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserService {
    User saveUser(User user);

    User saveAdmin(User user);

    User getUserByEmail(String email);

    List<User> getAllUsers(String role);

    Boolean updateUserAccountStatus(Integer id, Boolean status);

    // methods for failure attempt to login

    void countFailedAttempts(User user); // this is for getting the count of failed attempts

    void userAccountLock(User user);  // here we lock the user account and get the timestamp

    Boolean unlockAccountTimeExpired(User user);  // to check whether the account is unlocked or not

    public void resetAttempt(int id);       // methods for failure attempt to login

    void updateUserResetToken(String email, String resetToken);

    User getUserByToken(String resetToken);

    User updateUser(User user);

    User updateUserProfile(User user, MultipartFile image);

    List<User> searchUser(String ch);
}
