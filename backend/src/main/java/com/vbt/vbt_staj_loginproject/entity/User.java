package com.vbt.vbt_staj_loginproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
  Neden BaseEntity'den miras aliyoruz?
 id, createdAt, updatedAt alanlari otomatik gelir.
 */
@Entity
@Table(
    name = "users",
    indexes = {  //login sırasında email ile sorgulama yapilir. Index olmazsa her login'de tum tablo taranir
        @Index(name = "idx_users_email", columnList = "email", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;  // Argon2 ile

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

}
