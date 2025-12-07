package org.cms.carrental.service;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.AuthResponse;
import org.cms.carrental.dto.LoginRequest;
import org.cms.carrental.dto.RegisterRequest;
import org.cms.carrental.dto.UserDto;
import org.cms.carrental.entity.User;
import org.cms.carrental.repository.UserRepository;
import org.cms.carrental.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        UserDto userDto = userService.registerUser(request);

        // JWT Token oluştur
        String token = jwtUtil.generateToken(userDto.getEmail());

        return new AuthResponse(token, userDto);
    }

    /**
     * Admin kullanıcısı kaydet - Sadece mevcut admin tarafından çağrılabilir
     */
    @Transactional
    public AuthResponse registerAdmin(RegisterRequest request) {
        UserDto userDto = userService.registerAdmin(request);

        // JWT Token oluştur
        String token = jwtUtil.generateToken(userDto.getEmail());

        return new AuthResponse(token, userDto);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Spring Security ile authentication yap
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Authentication başarılı, kullanıcıyı getir
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDto userDto = userService.getUserByEmail(request.getEmail());

            // JWT Token oluştur
            String token = jwtUtil.generateToken(user.getEmail());

            return new AuthResponse(token, userDto);

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}

