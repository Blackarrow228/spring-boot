package org.example.springboot.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class Book {
    private UUID id;
    private String title;
    private String author;
    private LocalDate publicationYear;
}
