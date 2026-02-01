package com.SecureLibrarySystem.webapp.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecureLibrarySystem {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**").permitAll()
                .requestMatchers(
                        "/",
                        "/login-page",
                        "/register-page",
                        "/otp-page",
                        "/dashboard",
                        "/auth/**"
                ).permitAll()
                .anyRequest().permitAll()
            )

            .formLogin(form -> form.disable());

        return http.build();
    }
}

