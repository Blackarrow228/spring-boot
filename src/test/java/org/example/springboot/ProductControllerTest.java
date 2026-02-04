package org.example.springboot;

import org.example.springboot.controller.ProductController;
import org.example.springboot.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct() throws Exception {
        String json = """
            {
                "name": "Smartphone",
                "description": "Latest model",
                "price": 699.99,
                "stock": 50
            }
            """;

        when(productService.createProduct(json)).thenReturn(json);

        mockMvc.perform(post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().json(json));
    }

    @Test
    void getProduct() throws Exception {
        String jsonResponse = """
            {
                "id": 1,
                "productName": "Tablet",
                "price": 299.99
            }
            """;

        UUID productId = UUID.randomUUID();
        when(productService.getProduct(productId)).thenReturn(jsonResponse);

        mockMvc.perform(get("/api/v1/product/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse))
                .andDo(print());
    }
}
