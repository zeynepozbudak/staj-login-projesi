package com.vbt.vbt_staj_loginproject.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vbt.vbt_staj_loginproject.exception.ErrorResponse;
import com.vbt.vbt_staj_loginproject.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

//brute force koruması sağlar

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // LocalDateTime düzgün formatında yazmak için
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //istek yapan kişinin IP adresini alır
        String clientIp = getClientIp(request);

        //redise sorar 5 ten fala istek geldiyse 429 hatası döner
        if (rateLimitService.isRateLimited(clientIp)) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Retry-After", "60");

            ErrorResponse errorResponse = new ErrorResponse(
                    429,
                    "Cok fazla istek gonderdiniz, lutfen 1 dakika bekleyin",
                    null,
                    LocalDateTime.now()
            );

            response.setStatus(429);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        //bir sonrakiye filtreye geçirir
        filterChain.doFilter(request, response);
    }

    //sadece login ve register endpointlerine uygular
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !"/auth/login".equals(path) && !"/auth/register".equals(path);
    }

    /**
     istek atan kişinin  IP adresini alır
     eğer "X-Forwarded-For" başlığı varsa, bu başlıktan IP adresini alır
     yoksa doğrudan isteği yapan kişinin IP adresini döner
     */
    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}