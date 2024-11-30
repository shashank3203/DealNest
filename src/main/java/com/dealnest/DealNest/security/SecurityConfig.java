package com.dealnest.DealNest.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    @Lazy
    private AuthenticationFailureHandlerImpl authenticationFailureHandler;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests
                        (configurer ->
                                configurer // We are using opposite or normal method
                                        .requestMatchers("/dealnest/user/**").hasAnyRole("USER", "ADMIN")
                                        .requestMatchers("/dealnest/admin/**").hasRole("ADMIN")
                                        .requestMatchers("/**").permitAll()
                        )

                .formLogin
                        (formLogin ->
                                        formLogin.loginPage("/dealnest/login")
                                                .loginProcessingUrl("/user/authenticate")
                                                .failureHandler(authenticationFailureHandler)
                                                .successHandler(authenticationSuccessHandler)
//                    .failureUrl("/login?error=true")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                        )
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.permitAll())
                .exceptionHandling(configurer ->
                        configurer
                                .accessDeniedPage("/access-denied"));
        return httpSecurity.build();
    }
}