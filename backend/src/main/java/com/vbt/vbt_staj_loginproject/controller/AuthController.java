package com.vbt.vbt_staj_loginproject.controller;

import com.vbt.vbt_staj_loginproject.dto.request.LoginRequestDto;
import com.vbt.vbt_staj_loginproject.dto.request.RegisterRequestDto;
import com.vbt.vbt_staj_loginproject.dto.response.LoginResponseDto;
import com.vbt.vbt_staj_loginproject.dto.response.RefreshResponseDto;
import com.vbt.vbt_staj_loginproject.dto.response.RegisterResponseDto;
import com.vbt.vbt_staj_loginproject.exception.InvalidRefreshTokenException;
import com.vbt.vbt_staj_loginproject.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //yeni kullanıcı kaydı oluşur, refresh token cookie ye yazılır
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequestDto request,
                                                        HttpServletResponse httpResponse) {
        AuthService.AuthResult result = authService.register(request);
        addRefreshTokenCookie(httpResponse, result.refreshToken(), 7 * 24 * 60 * 60);
        return ResponseEntity.status(HttpStatus.CREATED).body((RegisterResponseDto) result.response());
    }

    // kullanıcı giriş yapar, refresh token cookie ye yazılır
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request,
                                                  HttpServletResponse httpResponse) {
        AuthService.AuthResult result = authService.login(request);
        addRefreshTokenCookie(httpResponse, result.refreshToken(), 7 * 24 * 60 * 60);
        return ResponseEntity.ok((LoginResponseDto) result.response());
    }

    // refresh token ile yeni access token alır, refresh token cookie ye yazılır
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDto> refresh(HttpServletRequest request, HttpServletResponse httpResponse) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new InvalidRefreshTokenException("Refresh token bulunamadı");
        }

        AuthService.AuthResult result = authService.refresh(refreshToken);
        addRefreshTokenCookie(httpResponse, result.refreshToken(), 7 * 24 * 60 * 60);
        return ResponseEntity.ok((RefreshResponseDto) result.response());
    }

    // kullanıcı çıkış yapar, refresh token cookie silinir
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse httpResponse) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        addRefreshTokenCookie(httpResponse, "", 0);
        return ResponseEntity.ok().build();
    }

    // refresh token cookie oluşturur
    private void addRefreshTokenCookie(HttpServletResponse httpResponse, String refreshToken, int maxAge) {
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/auth");
        refreshCookie.setMaxAge(maxAge);
        httpResponse.addCookie(refreshCookie);
    }

    // refresh token cookie den alır
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}