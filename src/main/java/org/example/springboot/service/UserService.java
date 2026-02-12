package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.UserDto;
import org.example.springboot.entity.User;
import org.example.springboot.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.springboot.mapper.UserMapper.mapToSecureUserDto;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void unlockUser(String username) {
        User user = userRepository.findUserByUsername(username)
                                  .orElseThrow(() -> getUsernameNotFoundException(username));
        user.setIsAccountNonLocked(true);
        user.setFailedLoginAttempts(0);
    }

    public UserDto getUser(String username) {
        User user = userRepository.findUserByUsername(username)
                                  .orElseThrow(() -> getUsernameNotFoundException(username));
        return mapToSecureUserDto(user);
    }

    @Transactional
    public void lockUser(String username) {
        userRepository.findUserByUsername(username)
                      .orElseThrow(() -> getUsernameNotFoundException(username))
                      .setIsAccountNonLocked(false);
    }

    private static UsernameNotFoundException getUsernameNotFoundException(String username) {
        return new UsernameNotFoundException("User не найден: " + username);
    }
}
