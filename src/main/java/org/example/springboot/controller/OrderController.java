package org.example.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.springboot.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/validate")
    public ResponseEntity<String> createOrder(@RequestBody String orderJson) {
            String json = orderService.createOrder(orderJson);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<String> getOrder(@PathVariable UUID orderId) {
        String json = orderService.getOrderById(orderId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }
}
