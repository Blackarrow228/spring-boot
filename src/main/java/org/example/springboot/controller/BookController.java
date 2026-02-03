package org.example.springboot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.request.BookRequest;
import org.example.springboot.dto.request.UpdateBookRequest;
import org.example.springboot.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BookDto> bookPage = bookService.getAllBooks(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("books", bookPage.getContent());
        response.put("currentPage", bookPage.getNumber());
        response.put("totalItems", bookPage.getTotalElements());
        response.put("totalPages", bookPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.createBook(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UUID> updateBook(
            @PathVariable UUID id,
            @RequestBody UpdateBookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UUID> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(id);
    }
}
