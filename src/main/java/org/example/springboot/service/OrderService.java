package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.Order;
import org.example.springboot.exception.BadRequestException;
import org.example.springboot.exception.NotFoundException;
import org.example.springboot.repository.OrderRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public String createOrder(String orderJson) {
        try {
            Order order = objectMapper.readValue(orderJson, Order.class);
            Order savedOrder = orderRepository.save(order);
            return objectMapper.writeValueAsString(savedOrder);
        } catch (Exception e) {
            throw new BadRequestException("не валидный JSON");
        }
    }

    public String getOrderById(UUID id) {
        try {
            Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("продукт с id: " + id + " не найден"));
            return objectMapper.writeValueAsString(order);
        } catch (Exception e) {
            throw new BadRequestException("не валидный JSON");
        }
    }
}
