package com.vbt.vbt_staj_loginproject.controller;

import com.vbt.vbt_staj_loginproject.dto.request.LoginRequestDto;
import com.vbt.vbt_staj_loginproject.dto.request.RegisterRequestDto;
import com.vbt.vbt_staj_loginproject.dto.response.LoginResponseDto;
import com.vbt.vbt_staj_loginproject.dto.response.RegisterResponseDto;
import com.vbt.vbt_staj_loginproject.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequestDto request,
                                                        HttpServletResponse httpResponse) {

        // serviceten auth result alınır
        AuthService.AuthResult result = authService.register(request);

        // refresh token cookie olarak eklenir
        Cookie refreshCookie = new Cookie("refreshToken", result.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/auth");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        httpResponse.addCookie(refreshCookie);

        return ResponseEntity.status(HttpStatus.CREATED).body((RegisterResponseDto) result.response()); //201 yeni kayıt oluştu
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request,
                                                  HttpServletResponse httpResponse) {

        AuthService.AuthResult result = authService.login(request);

        // refresh token cookie olarak eklenir
        Cookie refreshCookie = new Cookie("refreshToken", result.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/auth");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        httpResponse.addCookie(refreshCookie);

        return ResponseEntity.ok((LoginResponseDto) result.response());
    }
}
