package org.cms.carrental.config;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.security.CustomUserDetailsService;
import org.cms.carrental.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost:8080", "https://car-rental-front-flax.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(401, "Unauthorized");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Thymeleaf sayfaları - Server-side rendered pages (herkese açık)
                        .requestMatchers("/", "/cars-page", "/cars-page/**", "/error").permitAll()

                        // Statik kaynaklar (CSS, JS, images)
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Public endpoints - Register ve Login herkese açık
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        // Auth check endpoints - authenticated users
                        .requestMatchers("/auth/check", "/auth/check-admin").authenticated()
                        // Currency API - Döviz kurları herkese açık (External API)
                        .requestMatchers("/currency/**").permitAll()

                        // Cars - GET herkese açık (herkes arabaları görebilir)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/cars", "/cars/**").permitAll()
                        // Cars - POST/PUT/DELETE sadece ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/cars").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/cars/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/cars/**").hasRole("ADMIN")

                        // Categories - GET herkese açık (herkes kategorileri görebilir)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/categories", "/categories/**").permitAll()
                        // Categories - POST/PUT/DELETE sadece ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/categories").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                        // Reservations - Authenticated users (Giriş yapan kullanıcılar rezervasyon yapabilir)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/reservations", "/reservations/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/reservations").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/reservations/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/reservations/**").hasRole("ADMIN")

                        // Rentals - Admin only (Kiralama işlemleri sadece admin)
                        .requestMatchers("/rentals/**").hasRole("ADMIN")

                        // Users - /me endpoint'i authenticated kullanıcılar için, diğerleri admin
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // Diğer tüm istekler authentication gerektirir
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

