package org.example.springboot;

import org.example.springboot.controller.BookController;
import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.request.BookRequest;
import org.example.springboot.dto.request.UpdateBookRequest;
import org.example.springboot.exception.DuplicateException;
import org.example.springboot.exception.NotFoundException;
import org.example.springboot.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    private UUID bookId;
    private UUID authorId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        authorId = UUID.randomUUID();
    }

    @Test
    @DisplayName("POST - успешное создание book")
    void createBook_201_test() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("abc");
        request.setAuthorName("fjal");
        BookDto bookDto = BookDto.builder()
                .title(request.getTitle())
                .id(bookId)
                .name(request.getAuthorName())
                .authorId(authorId)
                .build();

        when(bookService.createBook(any(BookRequest.class))).thenReturn(bookDto);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(bookDto)))
                .andDo(print());

        verify(bookService, times(1)).createBook(any(BookRequest.class));
    }

    @Test
    @DisplayName("GET - успешное получение book")
    void getBook_200_test() throws Exception {
        BookDto bookDto = BookDto.builder()
                .title("abc")
                .id(bookId)
                .name("fjal")
                .authorId(authorId)
                .build();

        when(bookService.getBookById(eq(bookId))).thenReturn(bookDto);

        mockMvc.perform(get("/api/v1/books/" + bookId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookDto)))
                .andDo(print());

        verify(bookService, times(1)).getBookById(eq(bookId));
    }

    @Test
    @DisplayName("PATCH - успешное обновление book")
    void patchBook_200_test() throws Exception {
        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("abc");
        request.setAuthorName("fjalf");

        when(bookService.updateBook(eq(bookId), any(UpdateBookRequest.class))).thenReturn(bookId);

        mockMvc.perform(patch("/api/v1/books/" + bookId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(bookId.toString()))
                .andDo(print());

        verify(bookService, times(1)).updateBook(eq(bookId), any(UpdateBookRequest.class));
    }

    @Test
    @DisplayName("DELETE - успешное удаление book")
    void deleteBook_200_test() throws Exception {

        doNothing().when(bookService).deleteBook(eq(bookId));

        mockMvc.perform(delete("/api/v1/books/" + bookId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(bookId.toString()))
                .andDo(print());

        verify(bookService, times(1)).deleteBook(eq(bookId));
    }

    @Test
    @DisplayName("GET - получение книг с пагинацией")
    void getAllBooks_withPagination_200_test() throws Exception {
        int page = 0;
        int size = 5;

        List<BookDto> books = Arrays.asList(
                BookDto.builder()
                        .id(bookId)
                        .title("abc")
                        .name("fjalf")
                        .authorId(authorId)
                        .build(),
                BookDto.builder()
                        .id(bookId)
                        .title("cba")
                        .name("fjal")
                        .authorId(authorId)
                        .build()
        );

        Page<BookDto> bookPage = new PageImpl<>(
                books,
                PageRequest.of(page, size),
                books.size()
        );

        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookPage);

        mockMvc.perform(get("/api/v1/books")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", "title,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.books[0].id").value(bookId.toString()))
                .andExpect(jsonPath("$.books[1].id").value(bookId.toString()))
                .andExpect(jsonPath("$.books[0].name").value("fjalf"))
                .andExpect(jsonPath("$.books[0].title").value("abc"))
                .andExpect(jsonPath("$.books[1].name").value("fjal"))
                .andExpect(jsonPath("$.books[1].title").value("cba"))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0));

        verify(bookService, times(1)).getAllBooks(any(Pageable.class));
    }

    @Test
    @DisplayName("PATCH - NotFound book")
    void patchBook_404_notFoundBook_test() throws Exception {
        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("abc");
        request.setAuthorName("fjalf");

        when(bookService.updateBook(eq(bookId), any(UpdateBookRequest.class))).thenThrow(new NotFoundException("книга не найдена"));

        mockMvc.perform(patch("/api/v1/books/" + bookId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("книга не найдена"))
                .andDo(print());

        verify(bookService, times(1)).updateBook(eq(bookId), any(UpdateBookRequest.class));
    }

    @Test
    @DisplayName("GET - NotFound book")
    void getBook_404_notFoundBook_test() throws Exception {
        when(bookService.getBookById(eq(bookId))).thenThrow(new NotFoundException("книга не найдена"));

        mockMvc.perform(get("/api/v1/books/" + bookId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("книга не найдена"))
                .andDo(print());

        verify(bookService, times(1)).getBookById(eq(bookId));
    }

    @Test
    @DisplayName("POST - Duplicate book")
    void createBook_400_DuplicateBook_test() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("abc");
        request.setAuthorName("fjal");

        when(bookService.createBook(any(BookRequest.class))).thenThrow(new DuplicateException("книга с таким названием уже существует"));

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("книга с таким названием уже существует"))
                .andDo(print());

        verify(bookService, times(1)).createBook(any(BookRequest.class));
    }
}
