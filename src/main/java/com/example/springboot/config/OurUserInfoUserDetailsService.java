package com.example.springboot.config;

import com.example.springboot.model.OurUser;
import com.example.springboot.repository.OurUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class OurUserInfoUserDetailsService implements UserDetailsService {
    @Autowired
    private OurUserRepo ourUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Nu mai este nevoie să folosim Optional.ofNullable aici, presupunând că findByEmail returnează un Optional<OurUser>
        OurUser user = ourUserRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Does Not Exist"));
        return new OurUserInfoDetails(user);
    }
}