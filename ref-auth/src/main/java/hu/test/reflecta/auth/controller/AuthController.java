package hu.test.reflecta.auth.controller;

import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid final LoginRequest request) {
        final Optional<LoginResponse> response = authService.login(request);
        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
