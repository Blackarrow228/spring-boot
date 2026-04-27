package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.Product;
import org.example.springboot.exception.BadRequestException;
import org.example.springboot.exception.NotFoundException;
import org.example.springboot.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(cacheNames = "allProduct")
    public String getAllProducts() {
        List<Product> products = productRepository.findAll();
        try {
            return objectMapper.writeValueAsString(products);
        } catch (Exception e) {
            throw new BadRequestException("не валидный JSON");
        }
    }
//sync нужен для предотвращения cache stamped что бы только один поток пошел в бд и обновил кэш, пока другие будут ждать
    @Cacheable(cacheNames = "product", key = "#productId", sync = true)
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
    @CachePut(cacheNames = "product", key = "#product.productId")
    @CacheEvict(cacheNames = "allProduct", allEntries = true)
    public String createProduct(Product product) {
        try {
            Product savedProduct = productRepository.save(product);
            return objectMapper.writeValueAsString(savedProduct);
        } catch (Exception e) {
            throw new BadRequestException("не валидный JSON");
        }
    }

    @Transactional
    @CachePut(cacheNames = "product", key = "#productId")
    @CacheEvict(cacheNames = "allProduct", allEntries = true)
    public String updateProduct(UUID productId, Product updateProduct) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("продукт с id: " + productId + " не найден"));
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
    @CacheEvict(cacheNames = "product", key = "#productId")
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("продукт с id: " + productId + " не найден"));
        productRepository.delete(product);
    }
}
