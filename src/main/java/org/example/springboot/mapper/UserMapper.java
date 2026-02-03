package org.example.springboot.mapper;

import org.example.springboot.entity.User;
import org.example.springboot.request.UserRequest;
import org.example.springboot.response.UserResponse;

import java.util.List;

import static org.example.springboot.mapper.OrderMapper.mapToOrderDtoList;

public class UserMapper {

    public static UserResponse mapToSummaryUserResponse(User user) {
        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserResponse> mapToSummaryUserResponseList(List<User> users) {
        return users.stream()
                .map(UserMapper::mapToSummaryUserResponse)
                .toList();
    }

    public static UserResponse mapToDetailUserResponse(User user) {
        UserResponse userResponse = mapToSummaryUserResponse(user);
        userResponse.setOrders(mapToOrderDtoList(user.getOrders()));
        return userResponse;
    }

    public static User mapToUser(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }
}
