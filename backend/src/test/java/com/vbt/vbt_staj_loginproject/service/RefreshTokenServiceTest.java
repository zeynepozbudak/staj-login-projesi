package com.vbt.vbt_staj_loginproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private UUID userId;
    private String refreshToken;
    private String expectedKey;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        refreshToken = "test-refresh-token-value";
        expectedKey = "refresh:" + userId.toString();
    }

    @Test
    @DisplayName("Refresh token Redis'e doğru key ve TTL ile kaydedilmeli")
    void shouldSaveRefreshTokenToRedis() {
        //redisTemplate.opsForValue() çağrılınca mock valueOperations dönsün
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        refreshTokenService.save(userId, refreshToken);

        //doğru key ve TTL ile set metodunun çağrıldığını doğrula
        verify(valueOperations).set(expectedKey, refreshToken, Duration.ofDays(7));
    }

    @Test
    @DisplayName("Geçerli token için true dönmeli")
    void shouldReturnTrueWhenTokenIsValid() {
        //Redisten aynı token dönsün
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(expectedKey)).thenReturn(refreshToken);

        boolean result = refreshTokenService.isValid(userId, refreshToken);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Farklı token için false dönmeli")
    void shouldReturnFalseWhenTokenDoesNotMatch() {
        //Redisten farklı bir token dönsün
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(expectedKey)).thenReturn("farkli-bir-token");

        boolean result = refreshTokenService.isValid(userId, refreshToken);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Redis'te token yoksa false dönmeli")
    void shouldReturnFalseWhenTokenNotInRedis() {
        //Redisten null dönsün
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(expectedKey)).thenReturn(null);

        boolean result = refreshTokenService.isValid(userId, refreshToken);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Delete çağrılınca doğru key silinmeli")
    void shouldDeleteRefreshTokenFromRedis() {

        refreshTokenService.delete(userId);

        verify(redisTemplate).delete(expectedKey);
    }
}