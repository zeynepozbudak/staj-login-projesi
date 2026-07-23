package com.vbt.vbt_staj_loginproject.service;

import com.vbt.vbt_staj_loginproject.dto.request.LoginRequestDto;
import com.vbt.vbt_staj_loginproject.dto.request.RegisterRequestDto;
import com.vbt.vbt_staj_loginproject.dto.response.LoginResponseDto;
import com.vbt.vbt_staj_loginproject.dto.response.RegisterResponseDto;
import com.vbt.vbt_staj_loginproject.dto.response.RefreshResponseDto;
import com.vbt.vbt_staj_loginproject.entity.User;
import com.vbt.vbt_staj_loginproject.exception.EmailAlreadyExistException;
import com.vbt.vbt_staj_loginproject.exception.InvalidRefreshTokenException;
import com.vbt.vbt_staj_loginproject.repository.UserRepository;
import com.vbt.vbt_staj_loginproject.security.JwtService;
import com.vbt.vbt_staj_loginproject.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail("meryem@test.com");
        testUser.setPassword("hashedPassword");
        testUser.setFirstName("Meryem");
        testUser.setLastName("Test");
    }

    @Test
    @DisplayName("Başarılı kayıt — user kaydedilmeli, token üretilmeli, Redis'e yazılmalı")
    void shouldRegisterSuccessfully() {

        //frontendin gönderdiği request simülasyonu
        RegisterRequestDto request = new RegisterRequestDto(
                "Meryem", "Test", "meryem@test.com", "password123"
        );

        when(userRepository.existsByEmail("meryem@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(testUserId, "meryem@test.com")).thenReturn("access-token");
        when(jwtService.generateRefreshToken(testUserId, "meryem@test.com")).thenReturn("refresh-token");

        AuthService.AuthResult result = authService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.refreshToken()).isEqualTo("refresh-token");

        RegisterResponseDto response = (RegisterResponseDto) result.response();
        assertThat(response.email()).isEqualTo("meryem@test.com");
        assertThat(response.firstName()).isEqualTo("Meryem");
        assertThat(response.accessToken()).isEqualTo("access-token");

        //redise kaydedildi mi
        verify(refreshTokenService).save(testUserId, "refresh-token");

        //şifre hashlendi mi ve user kaydedildi mi
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Email zaten kayıtlıysa exception fırlatmalı")
    void shouldThrowExceptionWhenEmailExists() {

        RegisterRequestDto request = new RegisterRequestDto(
                "Meryem", "Test", "meryem@test.com", "password123"
        );

        when(userRepository.existsByEmail("meryem@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistException.class);

        //user kaydedilmemeli
        verify(userRepository, never()).save(any(User.class));

        //token üretilmemeli
        verify(jwtService, never()).generateAccessToken(any(), any());

        //redise yazılmamalı
        verify(refreshTokenService, never()).save(any(), any());
    }

    @Test
    @DisplayName("Başarılı giriş — token üretilmeli, Redis'e yazılmalı")
    void shouldLoginSuccessfully() {

        LoginRequestDto request = new LoginRequestDto("meryem@test.com", "password123");
        UserPrincipal userPrincipal = new UserPrincipal(testUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtService.generateAccessToken(testUserId, "meryem@test.com")).thenReturn("access-token");
        when(jwtService.generateRefreshToken(testUserId, "meryem@test.com")).thenReturn("refresh-token");

        AuthService.AuthResult result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.refreshToken()).isEqualTo("refresh-token");

        LoginResponseDto response = (LoginResponseDto) result.response();
        assertThat(response.email()).isEqualTo("meryem@test.com");
        assertThat(response.accessToken()).isEqualTo("access-token");

        //redise kaydedildi mi
        verify(refreshTokenService).save(testUserId, "refresh-token");
    }


    @Test
    @DisplayName("Başarılı refresh — yeni token çifti üretilmeli, Redis güncellenmeli")
    void shouldRefreshSuccessfully() {

        String oldRefreshToken = "old-refresh-token";

        when(jwtService.extractEmail(oldRefreshToken)).thenReturn("meryem@test.com");
        when(jwtService.extractUserId(oldRefreshToken)).thenReturn(testUserId);
        when(refreshTokenService.isValid(testUserId, oldRefreshToken)).thenReturn(true);
        when(jwtService.generateAccessToken(testUserId, "meryem@test.com")).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(testUserId, "meryem@test.com")).thenReturn("new-refresh-token");


        AuthService.AuthResult result = authService.refresh(oldRefreshToken);

        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");

        RefreshResponseDto response = (RefreshResponseDto) result.response();
        assertThat(response.accessToken()).isEqualTo("new-access-token");

        //yeni refresh token redise kaydedildi mi
        verify(refreshTokenService).save(testUserId, "new-refresh-token");
    }

    @Test
    @DisplayName("Geçersiz refresh token'da exception fırlatmalı")
    void shouldThrowExceptionWhenRefreshTokenInvalid() {

        String invalidToken = "invalid-refresh-token";

        when(jwtService.extractEmail(invalidToken)).thenReturn("meryem@test.com");
        when(jwtService.extractUserId(invalidToken)).thenReturn(testUserId);
        when(refreshTokenService.isValid(testUserId, invalidToken)).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(invalidToken))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verify(jwtService, never()).generateAccessToken(any(), any());
    }


    @Test
    @DisplayName("Logout — Redis'ten token silinmeli")
    void shouldLogoutSuccessfully() {

        String refreshToken = "refresh-token";

        when(jwtService.extractUserId(refreshToken)).thenReturn(testUserId);

        authService.logout(refreshToken);

        //redisten silindi mi
        verify(refreshTokenService).delete(testUserId);
    }
}