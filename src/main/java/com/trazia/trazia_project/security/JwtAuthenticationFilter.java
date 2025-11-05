package com.trazia.trazia_project.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("🛡️ JWT Filter - Path: {}, Method: {}, Auth Header: {}",
                request.getServletPath(), request.getMethod(),
                request.getHeader("Authorization") != null ? "PRESENT" : "MISSING");

        String path = request.getServletPath();
        if (path.startsWith("/api/auth/") || path.startsWith("/h2-console/")) {
            log.info("⏭️  Saltando JWT Filter para ruta pública: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("🚫 No JWT token found or invalid format for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7).trim();
        final String username = jwtTokenProvider.extractUsername(jwt);

        log.info("🔐 Processing JWT for user: {}, Path: {}", username, path);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtTokenProvider.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // LOGS DE DEBUG AGREGADOS
                    log.info("✅ Authentication established for user: {}", username);
                    log.info("✅ User authorities: {}", userDetails.getAuthorities());
                    log.info("✅ Request path: {}", request.getServletPath());
                } else {
                    log.warn("❌ Invalid JWT token for user '{}'", username);
                }
            } catch (Exception ex) {
                log.error("❌ Failed to authenticate user '{}': {}", username, ex.getMessage());
            }
        } else {
            log.info("ℹ️  Authentication already exists or username is null");
        }

        filterChain.doFilter(request, response);
    }
}