package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.entity.User;
import org.example.springboot.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginUtils {

    private final UserRepository userRepository;

    @Transactional
    public void handleFailedLogin(String username) {
        try {
            userRepository.findUserByUsername(username)
                          .ifPresent(user -> {
                              int attempts = user.getFailedLoginAttempts() + 1;
                              user.setFailedLoginAttempts(attempts);
                              if (attempts >= 5) {
                                  user.setIsAccountNonLocked(false);
                              }
                          });
        } catch (Exception e) {
            log.error("Error handling failed login for user: {}", username, e);
        }
    }

    @Transactional
    public void resetFailedAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
        }
    }
}
