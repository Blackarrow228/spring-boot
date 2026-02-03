package org.example.springboot.mapper;

import org.example.springboot.dto.OrderDto;
import org.example.springboot.entity.Order;

import java.util.List;

public class OrderMapper {

    public static OrderDto mapToOrderDto(Order order) {
        return OrderDto.builder()
                .item(order.getItem())
                .amount(order.getAmount())
                .status(order.getStatus())
                .build();
    }

    public static List<OrderDto> mapToOrderDtoList(List<Order> order) {
        return order.stream()
                .map(OrderMapper::mapToOrderDto)
                .toList();
    }
}
