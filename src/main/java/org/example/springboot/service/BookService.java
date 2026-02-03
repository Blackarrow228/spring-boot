package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.request.BookRequest;
import org.example.springboot.dto.request.UpdateBookRequest;
import org.example.springboot.entity.Author;
import org.example.springboot.entity.Book;
import org.example.springboot.exception.DuplicateException;
import org.example.springboot.exception.NotFoundException;
import org.example.springboot.mapper.BookMapper;
import org.example.springboot.repository.AuthorRepository;
import org.example.springboot.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.example.springboot.mapper.BookMapper.mapToBookDto;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookMapper::mapToBookDto);
    }

    public BookDto getBookById(UUID id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("книга не найдена"));
        return mapToBookDto(book);
    }

    @Transactional
    public BookDto createBook(BookRequest request) {
        if (bookRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateException("книга с таким названием уже существует");
        }
        Author author;
        if (request.getAuthorId() != null) {
            author = authorRepository.findById(request.getAuthorId())
                    .orElseGet(Author::new);
        } else {
            author = new Author();
        }
        if (request.getAuthorName() != null && !request.getAuthorName().isBlank()) {
            author.setName(request.getAuthorName());
        }
        Book book = new Book();
        book.setTitle(request.getTitle());

        Book saveBook = bookRepository.save(book);
        return mapToBookDto(saveBook);
    }

    @Transactional
    public UUID updateBook(UUID bookId, UpdateBookRequest request) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("книга не найдена"));
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthorName() != null && !request.getAuthorName().isBlank()) {
            book.getAuthor().setName(request.getAuthorName());
        }
        return bookId;
    }

    @Transactional
    public void deleteBook(UUID bookId) {
        bookRepository.deleteById(bookId);
    }
}
