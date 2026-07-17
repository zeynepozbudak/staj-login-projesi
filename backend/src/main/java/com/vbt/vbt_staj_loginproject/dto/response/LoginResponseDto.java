package com.vbt.vbt_staj_loginproject.dto.response;

import java.util.UUID;

public record LoginResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String accessToken
) {}