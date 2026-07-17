package com.vbt.vbt_staj_loginproject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Email boş olamaz")
        @Email(message = "Geçerli bir email adresi girin")
        String email,

        @NotBlank(message = "Şifre boş olamaz")
        String password
) {}
