package org.example.springboot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID productId;
    @NotBlank(message = "название не должно быть пустым")
    @NotNull(message = "название не должно быть null")
    private String name;
    private String description;
    @NotNull(message = "цена не должна быть null")
    private BigDecimal price;
    @NotNull(message = "колличество не должно быть null")
    private Long quantityInStock;
}
