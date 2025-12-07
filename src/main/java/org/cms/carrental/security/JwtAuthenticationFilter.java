package org.cms.carrental.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.cms.carrental.entity.User;
import org.cms.carrental.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Authorization header'dan JWT token'ı çıkar
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("JWT Token extraction error: " + e.getMessage());
            }
        }

        // Token geçerliyse ve henüz authentication yapılmamışsa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Token'ı doğrula
            if (jwtUtil.validateToken(jwt, username)) {

                // Kullanıcıyı veritabanından al ve gerçek rolünü kullan
                User user = userRepository.findByEmail(username).orElse(null);

                if (user != null) {
                    // Kullanıcının gerçek rolünü al (ROLE_USER veya ROLE_ADMIN)
                    String role = "ROLE_" + user.getRole().name();

                    // Authentication token oluştur
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                        );

                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // SecurityContext'e authentication'ı set et
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    logger.debug("User authenticated: " + username + " with role: " + role);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

