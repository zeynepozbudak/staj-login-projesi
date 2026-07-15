package com.vbt.vbt_staj_loginproject.security;

import com.vbt.vbt_staj_loginproject.entity.User;
import com.vbt.vbt_staj_loginproject.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Kullanici bulunamadi") //saldırganlar için genel bir hata mesajı
                );

        return new UserPrincipal(user);
    }
}
