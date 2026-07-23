package com.vbt.vbt_staj_loginproject.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VBT Staj - Login API")
                        .description("JWT tabanli kimlik dogrulama API'si. " +
                                "Access token + Refresh token (HttpOnly Cookie) yapisi, " +
                                "Redis ile token yonetimi, Argon2 ile sifre hashleme.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Meryem")
                                .email("meryemozevren44@gmail.com")));
    }
}