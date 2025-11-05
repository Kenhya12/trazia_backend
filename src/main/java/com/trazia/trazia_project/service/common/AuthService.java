package com.trazia.trazia_project.service.common;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trazia.trazia_project.dto.auth.*;
import com.trazia.trazia_project.entity.user.User;
import com.trazia.trazia_project.exception.auth.InvalidCredentialsException;
import com.trazia.trazia_project.exception.auth.UserAlreadyExistsException;
import com.trazia.trazia_project.repository.user.UserRepository;
import com.trazia.trazia_project.security.JwtTokenProvider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final com.trazia.trazia_project.service.security.UserDetailsServiceImpl userDetailsService; // ← AGREGAR

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        boolean emailExists = userRepository.existsByEmail(request.getEmail());
        boolean usernameExists = userRepository.existsByUsername(request.getUsername());

        if (emailExists || usernameExists) {
            StringBuilder message = new StringBuilder("Cannot register user:");
            if (emailExists)
                message.append(" email already registered;");
            if (usernameExists)
                message.append(" username already taken;");
            throw new UserAlreadyExistsException(message.toString());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully with email: {}", savedUser.getEmail());

        // ✅ USAR UserDetailsServiceImpl para obtener el UserDetails correcto
        org.springframework.security.core.userdetails.UserDetails userDetails = 
            userDetailsService.loadUserByUsername(savedUser.getEmail());

        return buildAuthResponse(userDetails, savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = Objects.requireNonNull(userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password")));

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
            
            // ✅ El authentication.getPrincipal() ya es tu entidad User personalizada
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

            log.info("User authenticated successfully: {}", user.getEmail());
            log.info("🔐 UserDetails type: {}", userDetails.getClass().getName());
            log.info("🔐 UserDetails username: {}", userDetails.getUsername());

            return buildAuthResponse(userDetails, user);

        } catch (Exception e) {
            log.error("Authentication failed for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    private AuthResponse buildAuthResponse(
            @NonNull org.springframework.security.core.userdetails.UserDetails userDetails, @NonNull User user) {
        String token = jwtTokenProvider.generateToken(userDetails);
        log.info("🔐 TOKEN GENERATION DEBUG:");
        log.info("🔐   UserDetails type: {}", userDetails.getClass().getName());
        log.info("🔐   UserDetails username: '{}'", userDetails.getUsername());
        log.info("🔐   Generated token: {}", token);
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .build();
    }
}