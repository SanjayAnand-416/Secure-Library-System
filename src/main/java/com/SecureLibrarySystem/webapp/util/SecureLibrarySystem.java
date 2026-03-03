package com.SecureLibrarySystem.webapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.SecureLibrarySystem.webapp.filter.NoCacheFilter;

@Configuration
public class SecureLibrarySystem {

    @Autowired
    private NoCacheFilter noCacheFilter;

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

            .formLogin(form -> form.disable())

            // Add the no-cache filter to prevent back-button vulnerability
            .addFilterBefore(noCacheFilter, UsernamePasswordAuthenticationFilter.class)

            // Enable security headers including cache control
            .headers(headers -> headers
                .cacheControl(cache -> {})
            );

        return http.build();
    }
}

