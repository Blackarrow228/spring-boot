package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.User;
import org.example.springboot.exception.NotFoundException;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.repository.UserRepository;
import org.example.springboot.request.UserRequest;
import org.example.springboot.response.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.example.springboot.mapper.UserMapper.mapToDetailUserResponse;
import static org.example.springboot.mapper.UserMapper.mapToUser;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String ERROR_MESSAGE = "user с id: %s не найден";

    private final UserRepository repository;

    public List<UserResponse> getAllUser() {
        List<User> users = repository.findAll();
        if (users.isEmpty()) {
            throw new NotFoundException("список пользователей пуст");
        }
        return UserMapper.mapToSummaryUserResponseList(users);
    }

    public UserResponse getUser(UUID userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException(ERROR_MESSAGE.formatted(userId)));
        return mapToDetailUserResponse(user);
    }

    @Transactional
    public UUID createUser(UserRequest request) {
        return repository.save(mapToUser(request)).getId();
    }

    @Transactional
    public UUID patchUser(UserRequest request, UUID userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException(ERROR_MESSAGE.formatted(userId)));
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        return user.getId();
    }

    @Transactional
    public UUID deleteUser(UUID userId) {
        repository.deleteById(userId);
        return userId;
    }
}
