package org.cms.carrental.service;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.config.JwtUtil;
import org.cms.carrental.dto.AuthResponse;
import org.cms.carrental.dto.LoginRequest;
import org.cms.carrental.dto.RegisterRequest;
import org.cms.carrental.dto.UserDto;
import org.cms.carrental.entity.User;
import org.cms.carrental.exception.AuthenticationException;
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
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        UserDto userDto = userService.registerUser(request);

        // Generate JWT token
        String token = jwtUtil.generateToken(userDto.getEmail(), userDto.getRole());

        return new AuthResponse(token, userDto);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        UserDto userDto = userService.getUserByEmail(request.getEmail());

        // Generate JWT token
        String token = jwtUtil.generateToken(userDto.getEmail(), userDto.getRole());

        return new AuthResponse(token, userDto);
    }
}

