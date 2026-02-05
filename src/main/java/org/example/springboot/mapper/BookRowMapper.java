package org.example.springboot.mapper;

import org.example.springboot.entity.Book;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();
        book.setId(UUID.fromString(rs.getString("id")));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));

        java.sql.Date sqlDate = rs.getDate("publication_year"); // или "publicationYear"
        if (sqlDate != null) {
            book.setPublicationYear(sqlDate.toLocalDate());
        }

        return book;
    }
}