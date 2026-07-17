package com.vbt.vbt_staj_loginproject.service;

import com.vbt.vbt_staj_loginproject.dto.request.LoginRequestDto;
import com.vbt.vbt_staj_loginproject.dto.request.RegisterRequestDto;
import com.vbt.vbt_staj_loginproject.dto.response.LoginResponseDto;
import com.vbt.vbt_staj_loginproject.dto.response.RegisterResponseDto;
import com.vbt.vbt_staj_loginproject.entity.User;
import com.vbt.vbt_staj_loginproject.exception.EmailAlreadyExistException;
import com.vbt.vbt_staj_loginproject.repository.UserRepository;
import com.vbt.vbt_staj_loginproject.security.JwtService;
import com.vbt.vbt_staj_loginproject.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  //şifreyi Argon2 ile şifreliycez
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;  // bunu ekle


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    //AuthResult sınıfı register ve login işlemlerinin sonucunu temsil eder
    //response alanı işlem sonucunda döndürülen JSON yanıtını içerir
    //refreshToken alanı ise cookie olarak gönderilecek olan refresh tokenı içerir
    public record AuthResult(
            Object response,  //Json body
            String refreshToken  //httpOnly cookie
    ) {}

    public AuthResult register(RegisterRequestDto request) {

        //email kayıtlı mı kontrolü yapılır
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistException("Bu email adresi zaten kayitli");
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

    public AuthResult login(LoginRequestDto request) {

        // email ve şifre doğrulama işlemi yapılır
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        //doğrulama başarılı ise kullanıcı bilgilerini alınır
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        //token üretilir
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        LoginResponseDto response = new LoginResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                accessToken
        );

        return new AuthResult(response, refreshToken);
    }
}
