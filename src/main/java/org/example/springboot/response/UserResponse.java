package org.example.springboot.response;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.springboot.dto.OrderDto;
import org.example.springboot.view.Views;

import java.util.List;

@Builder
@Getter
@Setter
public class UserResponse {

    @JsonView(Views.UserSummary.class)
    private String name;
    @JsonView(Views.UserSummary.class)
    private String email;
    @JsonView(Views.UserDetails.class)
    private List<OrderDto> orders;
}
