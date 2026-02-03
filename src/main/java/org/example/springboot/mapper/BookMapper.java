package org.example.springboot.mapper;

import org.example.springboot.dto.BookDto;
import org.example.springboot.entity.Book;

public class BookMapper {

    public static BookDto mapToBookDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorId(book.getAuthor().getId())
                .name(book.getAuthor().getName())
                .build();
    }
}
