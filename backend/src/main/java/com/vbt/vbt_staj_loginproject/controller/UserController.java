package com.vbt.vbt_staj_loginproject.controller;

import com.vbt.vbt_staj_loginproject.dto.response.UserResponseDto;
import com.vbt.vbt_staj_loginproject.entity.User;
import com.vbt.vbt_staj_loginproject.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 Bu controllerda service katmanı yok
 Çünkü kullanıcı bilgisi zaten JwtFilter da token doğrulanırken SecurityContext'e konuluyor

*/

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Kullanici bilgi islemleri")
public class UserController {

    // giriş yapmış kullanıcının kendi bilgilerini döner
    @Operation(
            summary = "Kullanici bilgilerini getir",
            description = "Access token ile giris yapmis kullanicinin id, email, firstName ve lastName bilgilerini döner. Dashboard ve profil sayfasi icin kullanilir.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kullanici bilgileri basariyla döndü"),
            @ApiResponse(responseCode = "401", description = "Access token eksik veya gecersiz")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();

        UserResponseDto response = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );

        return ResponseEntity.ok(response);
    }
}