package org.example.springboot.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.springboot.dto.Message;

@Getter
@Setter
public class LoginResponse extends Message {
    private String token;
    private String refreshToken;
    private String expirationTime;
}
