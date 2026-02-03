package org.example.springboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BookRequest {
    @NotBlank(message = "заполните название книги")
    private String title;
    private UUID authorId;
    @NotBlank(message = "заполните имя автора")
    private String authorName;
}
