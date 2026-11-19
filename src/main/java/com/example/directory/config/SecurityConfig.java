package com.example.directory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())                               // REST + Basic Auth
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))          // для консоли H2
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/h2-console/**").permitAll()
                // чтение: любой вошедший пользователь
                .requestMatchers(HttpMethod.GET, "/api/contacts/**", "/api/departments/**").authenticated()
                // изменение контактов: HR и ADMIN
                .requestMatchers("/api/contacts/**").hasAnyRole("HR", "ADMIN")
                // отделы и пользователи: только ADMIN
                .requestMatchers("/api/departments/**", "/api/users/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            // при неверном пароле просто 401, без всплывающего окна браузера
            .httpBasic(b -> b.authenticationEntryPoint((req, res, ex) -> res.sendError(401)));
        return http.build();
    }
}
