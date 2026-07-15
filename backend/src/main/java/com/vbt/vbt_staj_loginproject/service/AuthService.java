package com.vbt.vbt_staj_loginproject.service;

import com.vbt.vbt_staj_loginproject.dto.request.RegisterRequestDto;
import com.vbt.vbt_staj_loginproject.dto.response.RegisterResponseDto;
import com.vbt.vbt_staj_loginproject.entity.User;
import com.vbt.vbt_staj_loginproject.repository.UserRepository;
import com.vbt.vbt_staj_loginproject.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  //şifreyi Argon2 ile şifreliycez
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public record AuthResult(
            RegisterResponseDto response,  //Json body
            String refreshToken  //httpOnly cookie
    ) {}

    public AuthResult register(RegisterRequestDto request) {

        //email kayıtlı mı kontrolü yapılır
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Bu email adresi zaten kayitli");
        }

        //şifre hashlenir
        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(hashedPassword);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        User savedUser = userRepository.save(user);

        //token üretilir
        String accessToken = jwtService.generateAccessToken(
                savedUser.getId(), savedUser.getEmail());

        String refreshToken = jwtService.generateRefreshToken(
                savedUser.getId(), savedUser.getEmail());

        RegisterResponseDto response = new RegisterResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                accessToken,
                savedUser.getCreatedAt()
        );

        return new AuthResult(response, refreshToken);
    }
}
