package org.cms.carrental.service;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.AuthResponse;
import org.cms.carrental.dto.LoginRequest;
import org.cms.carrental.dto.RegisterRequest;
import org.cms.carrental.dto.UserDto;
import org.cms.carrental.entity.User;
import org.cms.carrental.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        UserDto userDto = userService.registerUser(request);

        // Token oluşturma (şimdilik basit, ileride JWT kullanılacak)
        String token = generateToken(userDto.getEmail());

        return new AuthResponse(token, userDto);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        UserDto userDto = userService.getUserByEmail(request.getEmail());

        // Token oluşturma (şimdilik basit, ileride JWT kullanılacak)
        String token = generateToken(user.getEmail());

        return new AuthResponse(token, userDto);
    }

    private String generateToken(String email) {
        // Basit token (Geliştirme aşaması için)
        // İleride JWT (JSON Web Token) kullanılacak
        return "TEMP_TOKEN_" + email + "_" + System.currentTimeMillis();
    }
}

