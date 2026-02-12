package org.example.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.request.LoginRequest;
import org.example.springboot.dto.request.RegistrationRequest;
import org.example.springboot.dto.response.LoginResponse;
import org.example.springboot.dto.response.RegistrationResponse;
import org.example.springboot.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> registration(@RequestBody RegistrationRequest request) {
        RegistrationResponse registration = authService.registration(request);
        return ResponseEntity.ok(registration);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
