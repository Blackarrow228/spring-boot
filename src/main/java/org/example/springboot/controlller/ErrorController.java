package org.example.springboot.controlller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/403")
    public ResponseEntity<String> errorAccess() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаток прав доступа");
    }

    @GetMapping("/401")
    public ResponseEntity<String> errorAuthentication() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ошибки аутентификации");
    }
}
