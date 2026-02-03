package org.example.springboot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequest {
    private String title;
    private String authorName;
}
