package com.isdemirstaj.backend.controller;

import com.isdemirstaj.backend.dto.user.*;
import com.isdemirstaj.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin // React uygulamasından gelen istekleri engellememek (CORS) için
@RestController
@RequestMapping("/api/users") // Ana adres: http://localhost:8080/api/users
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. GİRİŞ YAPMA ENDPOINT'İ
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponse = userService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponse); // Başarılıysa HTTP 200 ve Tokenları döner
    }

    // 2. KAYIT OLMA ENDPOINT'İ
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserCreateDto createDto) {
        UserResponseDto registerResponse = userService.register(createDto);
        return ResponseEntity.ok(registerResponse); // Başarılıysa HTTP 200 döner
    }

    // 3. TOKEN YENİLEME (REFRESH TOKEN) ENDPOINT'İ
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refresh(@RequestBody TokenRefreshRequestDto refreshRequest) {
        TokenRefreshResponseDto refreshResponse = userService.refreshToken(refreshRequest);
        return ResponseEntity.ok(refreshResponse); // Yeni Access Token setini döner
    }
}