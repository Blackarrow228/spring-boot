package org.example.springboot.controlller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/user")
    public String getUser(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("login", principal.getAttribute("login"));
        model.addAttribute("id", principal.getAttribute("id"));
        model.addAttribute("role", principal.getAuthorities());
        return new ObjectMapper().writeValueAsString(model);
    }

    @GetMapping("/admin/users")
    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("users", userRepository.findAll().stream()
                                            .map(user -> {
                                                Map<String, Object> userMap = new HashMap<>();
                                                userMap.put("id", user.getId());
                                                userMap.put("login", user.getLogin());
                                                userMap.put("role", user.getRole());
                                                return userMap;
                                            })
                                            .toList());
        return response;
    }
}
