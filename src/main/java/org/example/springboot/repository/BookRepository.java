package org.example.springboot.repository;

import org.example.springboot.entity.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(UUID id);
    List<Book> findAll();
    int update(Book book);
    boolean deleteById(UUID id);
    boolean existById(UUID id);
}
