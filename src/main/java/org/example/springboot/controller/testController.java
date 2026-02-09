package org.example.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping("/home")
    public String getHome() {
        return "home";
    }

    @GetMapping("/voice")
    public String getVoice() {
        return "voice";
    }
}
