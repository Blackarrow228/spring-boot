package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.Book;
import org.example.springboot.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository repository;

    public Book save(Book book) {
        validateBook(book);
        if (book.getId() == null) {
            book.setId(UUID.randomUUID());
            return repository.save(book);
        }
        throw new IllegalArgumentException("Используйте метод update для существующей книги");
    }

    public int update(Book book) {
        validateBook(book);
        if (book.getId() == null) {
            throw new IllegalArgumentException("id не должен быть null");
        }
        if (!repository.existById(book.getId())) {
            throw new RuntimeException("Книга с id: " + book.getId() + " не найдена");
        }
        return repository.update(book);
    }

    public Book findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Книга с id: " + id + " не найдена"));
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public boolean delete(UUID id) {
        if (!repository.existById(id)) {
            throw new RuntimeException("Книга с id: " + id + " не найдена");
        }
        return repository.deleteById(id);
    }

    private void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название книги не может быть пустым");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Автор книги не может быть пустым");
        }
    }
}
