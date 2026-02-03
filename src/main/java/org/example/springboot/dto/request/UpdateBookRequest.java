package org.example.springboot.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBookRequest {
    private String title;
    private String authorName;
}
