package com.vbt.vbt_staj_loginproject.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    // Her testten önce çalışır
    @BeforeEach
    void setUp() {
        // Test için sahte değerler elle veriyoruz
        String secret = "TestSecretKeyForJwtServiceTestMustBe32Chars!!";
        long accessTokenExpiration = 900000;    // 15 dakika
        long refreshTokenExpiration = 604800000; // 7 gün

        jwtService = new JwtService(secret, accessTokenExpiration, refreshTokenExpiration);
    }

    @Test
    @DisplayName("Access token başarıyla üretilmeli")
    void shouldGenerateAccessToken() {
        // test verileri hazırlanır
        UUID userId = UUID.randomUUID();
        String email = "test@test.com";

        // metodu çağırılır
        String token = jwtService.generateAccessToken(userId, email);

        //sonuc kontrol edilir
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT formatı: header.payload.signature
    }

    @Test
    @DisplayName("Refresh token başarıyla üretilmeli")
    void shouldGenerateRefreshToken() {
        UUID userId = UUID.randomUUID();
        String email = "test@test.com";

        String token = jwtService.generateRefreshToken(userId, email);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Token'dan email doğru çıkarılmalı")
    void shouldExtractEmailFromToken() {
        UUID userId = UUID.randomUUID();
        String email = "meryem@test.com";

        String token = jwtService.generateAccessToken(userId, email);
        String extractedEmail = jwtService.extractEmail(token);

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("Token'dan userId doğru çıkarılmalı")
    void shouldExtractUserIdFromToken() {
        UUID userId = UUID.randomUUID();
        String email = "test@test.com";

        String token = jwtService.generateAccessToken(userId, email);
        UUID extractedUserId = jwtService.extractUserId(token);

        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Geçerli token için true dönmeli")
    void shouldReturnTrueForValidToken() {
        UUID userId = UUID.randomUUID();
        String email = "test@test.com";

        String token = jwtService.generateAccessToken(userId, email);

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("Bozuk token için false dönmeli")
    void shouldReturnFalseForInvalidToken() {
        assertThat(jwtService.isTokenValid("bozuk.token.string")).isFalse();
    }

    @Test
    @DisplayName("Süresi dolmuş token için false dönmeli")
    void shouldReturnFalseForExpiredToken() {
        // Expiration süresi 1 milisaniye olan bir JwtService oluşturulur
        JwtService shortLivedService = new JwtService(
                "TestSecretKeyForJwtServiceTestMustBe32Chars!!",
                1,  // anında expire olacak
                1
        );

        UUID userId = UUID.randomUUID();
        String token = shortLivedService.generateAccessToken(userId, "test@test.com");

        //token'ın expire olması için bekleme
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThat(shortLivedService.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("Bozuk token'dan email çıkarılırken exception fırlatılmalı")
    void shouldThrowExceptionWhenExtractingEmailFromInvalidToken() {
        assertThatThrownBy(() -> jwtService.extractEmail("bozuk.token"))
                .isInstanceOf(Exception.class);
    }
}