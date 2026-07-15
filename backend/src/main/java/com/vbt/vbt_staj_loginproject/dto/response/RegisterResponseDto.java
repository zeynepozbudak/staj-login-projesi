package com.vbt.vbt_staj_loginproject.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String accessToken,
        LocalDateTime createdAt
) {}