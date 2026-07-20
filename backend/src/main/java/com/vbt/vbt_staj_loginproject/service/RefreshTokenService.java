package com.vbt.vbt_staj_loginproject.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * bu sınıf refresh tokenları saklamak silmek ve doğrulamak için kullanılır
 */

@Service
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(UUID userId, String refreshToken) {
        String key = "refresh:" + userId.toString();
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofDays(7));
    }

    public boolean isValid(UUID userId, String refreshToken) {
        String key = "refresh:" + userId.toString();
        String storedToken = redisTemplate.opsForValue().get(key);
        return refreshToken.equals(storedToken);
    }

    public void delete(UUID userId) {
        String key = "refresh:" + userId.toString();
        redisTemplate.delete(key);
    }
}