package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.Product;
import org.example.springboot.exception.BadRequestException;
import org.example.springboot.exception.NotFoundException;
import org.example.springboot.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public String getAllProducts() {
        List<Product> products = productRepository.findAll();
        try {
            return objectMapper.writeValueAsString(products);
        } catch (Exception e) {
            throw new BadRequestException("не валидный JSON");
        }
    }

    public String getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("продукт с id: " + productId + " не найден"));
        try {
            return objectMapper.writeValueAsString(product);
        } catch (Exception e) {
            throw new BadRequestException("не валидный JSON");
        }
    }

    @Transactional
    public String createProduct(String productJson) {
        try {
            Product product = objectMapper.readValue(productJson, Product.class);
            Product savedProduct = productRepository.save(product);
            return objectMapper.writeValueAsString(savedProduct);
        } catch (Exception e) {
            throw new BadRequestException("не валидный JSON");
        }
    }

    @Transactional
    public String updateProduct(UUID productId, String productJson) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("продукт с id: " + productId + " не найден"));
            Product updateProduct = objectMapper.readValue(productJson, Product.class);
            if (updateProduct.getName() != null && !updateProduct.getName().isBlank()) {
                product.setName(updateProduct.getName());
            }
            if (updateProduct.getDescription() != null && !updateProduct.getDescription().isBlank()) {
                product.setName(updateProduct.getName());
            }
            if (updateProduct.getPrice() != null) {
                product.setName(updateProduct.getName());
            }
            if (updateProduct.getQuantityInStock() != null) {
                product.setName(updateProduct.getName());
            }
            return objectMapper.writeValueAsString(product);
        } catch (Exception e) {
            throw new BadRequestException("не валидный JSON");
        }
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("продукт с id: " + productId + " не найден"));
        productRepository.delete(product);
    }
}
