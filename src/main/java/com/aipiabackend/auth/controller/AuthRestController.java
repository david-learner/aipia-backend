package com.aipiabackend.auth.controller;

import com.aipiabackend.auth.controller.dto.LoginRequest;
import com.aipiabackend.auth.controller.dto.LoginResponse;
import com.aipiabackend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String accessToken = authService.login(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }
}