package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

/*
Обрабатывает запросы аутентификации и авторизации (вход, регистрация).
Принимает данные для входа (AuthRequest), возвращает токены и ответы (AuthResponse).
Использует AuthService для проверки и создания JWT.
*/

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
