package org.example.springboot.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.example.springboot.dto.Message;

@Getter
@Setter
public class RegistrationRequest extends Message {
    private String role;
    private boolean IsAccountNonLocked;
}
