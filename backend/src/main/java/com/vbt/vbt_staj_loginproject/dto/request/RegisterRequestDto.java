package com.vbt.vbt_staj_loginproject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record RegisterRequestDto(

        @NotBlank(message = "İsim boş olamaz")
        @Size(min = 2, max = 100, message = "İsim 2-100 karakter olmalı")
        String firstName,

        @NotBlank(message = "Soyisim boş olamaz")
        @Size(min = 2, max = 100, message = "Soyisim 2-100 karakter olmalı")
        String lastName,

        @NotBlank(message = "Email boş olamaz")
        @Email(message = "Geçerli bir email adresi girin")
        String email,

        @NotBlank(message = "Şifre boş olamaz")
        @Size(min = 8, max = 100, message = "Şifre en az 8 karakter olmalı")
        String password

) {}