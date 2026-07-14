package com.vbt.vbt_staj_loginproject.repository;

import com.vbt.vbt_staj_loginproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    //Login sirasinda kullanilir: email'den User entity'sine ulasiriz,
    //sonra sifre hash'ini karsilastiririz.
    Optional<User> findByEmail(String email);


    //Register sirasinda cift kaydi onler.
    boolean existsByEmail(String email);
}
