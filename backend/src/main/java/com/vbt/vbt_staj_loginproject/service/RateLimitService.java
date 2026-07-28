package com.vbt.vbt_staj_loginproject.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

//ip bazlı rate limit kontrolü yapar rediste tutar

@Service
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // istek sayısını artırır ve limite ulaşıp ulaşmadığını döner
    public boolean isRateLimited(String ip) {
        String key = "rate:" + ip;

        Long currentCount = redisTemplate.opsForValue().increment(key);

        //ilk istekte TTL ayarlar  1 dakika sonra otomatik silinir
        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        return currentCount != null && currentCount > MAX_REQUESTS;
    }
}