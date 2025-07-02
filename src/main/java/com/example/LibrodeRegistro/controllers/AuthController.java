package com.example.LibrodeRegistro.controllers;

import com.example.LibrodeRegistro.dto.JwtResponse;
import com.example.LibrodeRegistro.dto.LoginRequest;
import com.example.LibrodeRegistro.dto.RegisterRequest;
import com.example.LibrodeRegistro.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}