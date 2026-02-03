package org.example.springboot.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "укажите имя")
    private String name;
    @NotBlank(message = "укажите email")
    @Email(message = "неверный формат email")
    private String email;
}
