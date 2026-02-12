package org.example.springboot.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.Log;
import org.example.springboot.dto.response.LoginResponse;
import org.example.springboot.entity.User;
import org.example.springboot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) {
        if (isLoginRequest(request)) {
            try {
                var requestWrapper = new ContentCachingRequestWrapper(request);
                var responseWrapper = new ContentCachingResponseWrapper(response);

                filterChain.doFilter(requestWrapper, responseWrapper);
                String responseBody = getBody(responseWrapper);
                String requestBody = getBody(requestWrapper);
                LoginResponse loginResponse = extractLoginResponse(responseBody);
                String username = extractUsername(requestBody);
                int status = 0;
                if (loginResponse != null) {
                    status = loginResponse.getStatusCode();
                }
                if (status == 200) {
                    logSuccess(username);
                    resetFailedAttempts(username);
                } else {
                    User user = userRepository.findUserByUsername(username)
                                              .orElse(null);
                    logFailure(username, status, user);

                    if (user != null && !user.isAccountNonLocked()) {
                        logLockout(username, user.getFailedLoginAttempts());
                    }
                }

                responseWrapper.copyBodyToResponse();

            } catch (Exception e) {
                logger.error("Ошибка логирования", e);
            }
        } else {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                logger.error("Ошибка фильтра", e);
            }
        }
    }

    private String getBody(Object wrapper) throws UnsupportedEncodingException {
        if (wrapper instanceof ContentCachingRequestWrapper) {
            byte[] content = ((ContentCachingRequestWrapper)wrapper).getContentAsByteArray();
            return new String(content, ((ContentCachingRequestWrapper)wrapper).getCharacterEncoding());
        }
        byte[] content = ((ContentCachingResponseWrapper)wrapper).getContentAsByteArray();
        return new String(content, ((ContentCachingResponseWrapper)wrapper).getCharacterEncoding());
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        return "POST".equalsIgnoreCase(method) && uri.endsWith("/auth/login");
    }

    private LoginResponse extractLoginResponse(String body) {
        try {
            if (body.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(body, LoginResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractUsername(String body) {
        return Objects.requireNonNull(extractLoginResponse(body))
                      .getUsername();
    }

    private void logSuccess(String username) {
        Log log = Log.builder()
                     .eventType("LOGIN_SUCCESS")
                     .username(username)
                     .statusCode(200)
                     .message("Успешный вход в систему")
                     .build();

        writeToFile(log);
        logger.info("LOGIN_SUCCESS: {}", username);
    }

    private void logFailure(String username, int status, User user) {
        String message = "Неверный логин или пароль";
        int attempts = 0;

        if (user != null) {
            attempts = user.getFailedLoginAttempts();
            message = String.format("Неверный пароль (попытка %d/5)", attempts);
        }

        Log log = Log.builder()
                     .eventType("LOGIN_FAILURE")
                     .username(username != null ? username : "unknown")
                     .statusCode(status)
                     .attempt(attempts)
                     .message(message)
                     .build();

        writeToFile(log);
        logger.warn("LOGIN_FAILURE: {} - {}", username, message);
    }

    private void logLockout(String username, int attempts) {
        Log log = Log.builder()
                     .eventType("ACCOUNT_LOCKED")
                     .username(username)
                     .statusCode(403)
                     .attempt(attempts)
                     .message("Аккаунт заблокирован после 5 неудачных попыток")
                     .build();

        writeToFile(log);
        logger.error("ACCOUNT_LOCKED: {} - заблокирован после {} попыток", username, attempts);
    }

    private void resetFailedAttempts(String username) {
        try {

            Log log = Log.builder()
                         .eventType("ATTEMPTS_RESET")
                         .username(username)
                         .statusCode(200)
                         .message("Счетчик неудачных попыток сброшен")
                         .build();
            writeToFile(log);

        } catch (Exception e) {
            logger.error("Ошибка сброса попыток {}", username, e);
        }
    }

    private void writeToFile(Log log) {
        try {
            Path logDir = Paths.get("logs");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            String json = objectMapper.writeValueAsString(log) + System.lineSeparator();
            Path logFile = Paths.get("logs/login.log");

            Files.write(logFile, json.getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            logger.error("Ошибка записи лога", e);
        }
    }
}
