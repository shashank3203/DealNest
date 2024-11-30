package com.dealnest.DealNest.security;

import com.dealnest.DealNest.entity.User;
import com.dealnest.DealNest.helper.AppConstant;
import com.dealnest.DealNest.repository.UserRepository;
import com.dealnest.DealNest.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("email");
        User user = userRepository.findByEmail(email);

        if (user!=null) {
            if (user.getIsEnabled()) {
                if (user.getAccountNonLocked()) {
                    if (user.getFailedAttempts() < AppConstant.ATTEMPT_TIME) {
                        userService.countFailedAttempts(user);
                    } else {
                        userService.userAccountLock(user);
                        exception = new LockedException("Your Account has been locked");
                    }
                } else {
                    if (userService.unlockAccountTimeExpired(user)) {
                        exception = new LockedException("Your Account has been unlocked || Please try to login again");
                    } else {
                        exception = new LockedException("Your Account has been locked || Please try to again later");
                    }
                    exception = new LockedException("Your Account has been locked");
                }
            } else {
                exception = new LockedException("Your Account is inactive");
            }
        } else {
            exception = new LockedException("Invalid email. Please Sign Up if you are a new user.");
        }

        super.setDefaultFailureUrl("/dealnest/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
