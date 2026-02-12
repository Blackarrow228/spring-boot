package org.example.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.service.CustomUserDetailsService;
import org.example.springboot.service.JWTUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    public static final int TOKEN_VALUE_START_POSITION = 7;
    private final JWTUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && !authHeader.isBlank()) {
                final String jwtToken = authHeader.substring(TOKEN_VALUE_START_POSITION);
                final String username = jwtUtils.extractUsername(jwtToken);
                if (username != null && SecurityContextHolder.getContext()
                                                             .getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                        var token = new UsernamePasswordAuthenticationToken(userDetails, null,
                                                                            userDetails.getAuthorities()
                        );
                        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        securityContext.setAuthentication(token);
                        SecurityContextHolder.setContext(securityContext);
                    } else {
                        throw new JwtException("Аккаунт заблокирован");
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token expired: {}", e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 401);
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", "JWT token expired");
            errorResponse.put("timestamp", LocalDateTime.now()
                                                        .toString());

            response.getWriter()
                    .write(new ObjectMapper().writeValueAsString(errorResponse));
        } catch (JwtException e) {
            log.warn(e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");

            response.getWriter()
                    .write(e.getMessage());
        } catch (UsernameNotFoundException e) {
            log.warn(e.getMessage());

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 404);
            errorResponse.put("error", "UsernameNotFound");
            errorResponse.put("message", e.getMessage());

            response.getWriter()
                    .write(new ObjectMapper().writeValueAsString(errorResponse));
        }
    }
}
