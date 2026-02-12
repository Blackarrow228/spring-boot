package org.example.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.springboot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorController {

    private final UserService userService;

    @PostMapping("/lock/{username}")
    @Secured(value = "MODERATOR")
    public ResponseEntity<String> lockUser(@PathVariable String username) {
        userService.lockUser(username);
        return ResponseEntity.ok("User: " + username + " заблокирован");
    }
}
