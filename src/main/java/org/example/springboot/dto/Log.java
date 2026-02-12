package org.example.springboot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Log {
    private String eventType;
    private String username;
    private int statusCode;
    private int attempt;
    private String message;
}
