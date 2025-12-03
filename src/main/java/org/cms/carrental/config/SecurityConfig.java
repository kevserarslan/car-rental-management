package org.cms.carrental.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            // Public endpoints
                            .requestMatchers("/api/auth/**").permitAll()
                            
                            // Car endpoints - read operations are public, write operations require authentication
                            .requestMatchers(HttpMethod.GET, "/api/cars/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/cars/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/cars/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasRole("ADMIN")
                            
                            // Category endpoints - read operations are public, write operations require admin
                            .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                            
                            // Reservation endpoints - require authentication
                            .requestMatchers("/api/reservations/**").authenticated()
                            
                            // Rental endpoints - require authentication
                            .requestMatchers("/api/rentals/**").authenticated()
                            
                            // User endpoints - require authentication
                            .requestMatchers("/api/users/**").authenticated()
                            
                            // Vehicle API endpoints - public for browsing
                            .requestMatchers("/api/vehicles/**").permitAll()
                            
                            // All other requests require authentication
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        } catch (Exception e) {
            throw new RuntimeException("Security configuration failed", e);
        }
    }
}

