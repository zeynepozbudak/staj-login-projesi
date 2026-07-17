package com.vbt.vbt_staj_loginproject.repository;

import com.vbt.vbt_staj_loginproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    //Login sirasinda kullanılır: emailden User entitysine ulasiriz
    //sonra şifre hashini karşılaştırırız
    Optional<User> findByEmail(String email);


    //Register sirasinda cift kaydi onler.
    boolean existsByEmail(String email);
}
