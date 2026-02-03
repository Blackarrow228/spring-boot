package org.example.springboot.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springboot.request.UserRequest;
import org.example.springboot.response.UserResponse;
import org.example.springboot.service.UserService;
import org.example.springboot.view.Views;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping()
    @JsonView(Views.UserSummary.class)
    public List<UserResponse> getAllUser() {
        return service.getAllUser();
    }

    @GetMapping("/{USER_UUID}/details")
    @JsonView(Views.UserDetails.class)
    public UserResponse getUser(@PathVariable(name = "USER_UUID") UUID userId) {
        return service.getUser(userId);
    }

    @PostMapping()
    public ResponseEntity<UUID> createUser(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createUser(request));
    }

    @PatchMapping("/{USER_UUID}")
    public ResponseEntity<String> patchUser(@PathVariable(name = "USER_UUID") UUID userId,
                                            @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body("user с id: " + service.patchUser(request, userId) + " обновлен");
    }

    @DeleteMapping("/delete/{USER_UUID}")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "USER_UUID") UUID userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body("user с id: " + service.deleteUser(userId) + " удален");
    }
}
