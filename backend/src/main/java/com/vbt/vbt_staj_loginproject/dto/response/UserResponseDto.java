package com.vbt.vbt_staj_loginproject.dto.response;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String email,
        String firstName,
        String lastName
) {
}
