package org.example.springboot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class BookDto {
    private UUID id;
    private String title;
    private UUID authorId;
    private String name;
}
