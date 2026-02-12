package org.example.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.UserDto;
import org.example.springboot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/unlock/{username}")
    @Secured(value = "SUPER_ADMIN")
    public ResponseEntity<String> unlockUser(@PathVariable String username) {
        userService.unlockUser(username);
        return ResponseEntity.ok("User: " + username + " разблокирован");
    }
}
