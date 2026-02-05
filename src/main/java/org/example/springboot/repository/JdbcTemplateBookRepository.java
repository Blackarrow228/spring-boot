package org.example.springboot.repository;

import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.Book;
import org.example.springboot.mapper.BookRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JdbcTemplateBookRepository implements BookRepository {

    private final JdbcTemplate template;
    private final BookRowMapper rowMapper;

    @Transactional
    @Override
    public Book save(Book book) {
        template.update(
                """
                        INSERT INTO books (id, title, author, publication_year)
                        VALUES (?, ?, ?, ?)
                        """,
                book.getId().toString(),
                book.getTitle(),
                book.getAuthor(),
                getDate(book)
        );
        return book;
    }

    @Override
    public boolean existById(UUID id) {
        Integer count = template.queryForObject(
                "SELECT COUNT(*) FROM books WHERE id = ?",
                Integer.class,
                id.toString()

        );
        return count != null && count > 0;
    }

    @Override
    public Optional<Book> findById(UUID id) {
        try {
            Book book = template.queryForObject(
                    """
                            SELECT id, title, author, publication_year
                            FROM books WHERE id = ?
                            """,
                    rowMapper,
                    id.toString()
            );
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        return template.query(
                """
                        SELECT id, title, author, publication_year
                        FROM books
                        """,
                rowMapper
        );
    }

    @Transactional
    @Override
    public int update(Book book) {
        return template.update(
                """
                        UPDATE books
                        SET title = ?, author = ?, publication_year = ?
                        WHERE id = ?
                        """,
                book.getTitle(),
                book.getAuthor(),
                getDate(book),
                book.getId().toString()
        );
    }

    @Transactional
    @Override
    public boolean deleteById(UUID id) {
        int rowsAffected = template.update(
                """
                        DELETE FROM books WHERE id = ?
                        """,
                id.toString()
        );
        return rowsAffected > 0;
    }

    private static Date getDate(Book book) {
        return book.getPublicationYear() != null
                ? Date.valueOf(book.getPublicationYear())
                : null;
    }
}
