package org.example.springboot;

import org.example.springboot.controller.ProductController;
import org.example.springboot.entity.Product;
import org.example.springboot.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct() throws Exception {
        Product product = new Product();
        product.setName("Smartphone");
        product.setDescription("Latest model");
        product.setPrice(new BigDecimal("699.99"));
        product.setQuantityInStock(50L);

        String json = objectMapper.writeValueAsString(product);
        when(productService.createProduct(product)).thenReturn(json);

        mockMvc.perform(post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().json(json));
    }

    @Test
    void getProduct() throws Exception {
        UUID productId = objectMapper.convertValue(1, UUID.class);
        Product product = new Product();
        product.setProductId(productId);
        product.setName("Tablet");
        product.setPrice(new BigDecimal("299.99"));
        String json = objectMapper.writeValueAsString(product);
        when(productService.getProduct(productId)).thenReturn(json);

        mockMvc.perform(get("/api/v1/product/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().json(json))
                .andDo(print());
    }
}
