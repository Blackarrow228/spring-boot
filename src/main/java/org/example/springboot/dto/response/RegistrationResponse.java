package org.example.springboot.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.springboot.dto.Message;
import org.example.springboot.dto.UserDto;

@Getter
@Setter
public class RegistrationResponse extends Message {
    private UserDto userDto;
}
