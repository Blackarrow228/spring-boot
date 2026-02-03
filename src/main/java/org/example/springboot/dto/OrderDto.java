package org.example.springboot.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.springboot.enums.OrderStatus;
import org.example.springboot.view.Views;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderDto {
    @JsonView(Views.UserDetails.class)
    private String item;
    @JsonView(Views.UserDetails.class)
    private BigDecimal amount;
    @JsonView(Views.UserDetails.class)
    private OrderStatus status;
}
