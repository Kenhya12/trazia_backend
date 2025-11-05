package com.trazia.trazia_project.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.trazia.trazia_project.repository.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username/email: {}", username);

        // Primero intentar por email
        com.trazia.trazia_project.entity.user.User appUser = userRepository.findByEmail(username)
                .orElseGet(() -> {
                    // Si no encuentra por email, intentar por username
                    log.debug("User not found by email '{}', trying by username", username);
                    return userRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
                });

        log.debug("User found: {} (email: {})", appUser.getUsername(), appUser.getEmail());
        return appUser;
    }
}