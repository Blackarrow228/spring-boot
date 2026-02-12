package org.example.springboot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.springboot.entity.component.RoleEnum;

@Getter
@Setter
@Builder
public class UserDto {
    private String username;
    private String password;
    private RoleEnum role;
    private Boolean isAccountNonLocked;
}
