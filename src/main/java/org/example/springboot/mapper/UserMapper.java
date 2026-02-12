package org.example.springboot.mapper;

import org.example.springboot.dto.UserDto;
import org.example.springboot.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                      .username(user.getUsername())
                      .password(user.getPassword())
                      .role(user.getRole())
                      .isAccountNonLocked(user.getIsAccountNonLocked())
                      .build();
    }

    public static UserDto mapToSecureUserDto(User user) {
        return UserDto.builder()
                      .username(user.getUsername())
                      .role(user.getRole())
                      .isAccountNonLocked(user.getIsAccountNonLocked())
                      .build();
    }
}
