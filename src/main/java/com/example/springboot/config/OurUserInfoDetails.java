package com.example.springboot.config;

import com.example.springboot.model.OurUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class OurUserInfoDetails implements UserDetails {
    private String username;
    private String password;
    private GrantedAuthority role;

    public OurUserInfoDetails(OurUser ourUser){
        this.username = ourUser.getUsername();
        this.password = ourUser.getPassword();
        this.role = new SimpleGrantedAuthority("ROLE_" + ourUser.getRoles()); // Preia rolul ca string și îl prelucrează direct
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(this.role); // Returnează o colecție cu un singur element
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
