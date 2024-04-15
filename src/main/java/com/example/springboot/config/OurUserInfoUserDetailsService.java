package com.example.springboot.config;

import com.example.springboot.model.OurUser;
import com.example.springboot.repository.OurUserRepo;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OurUserInfoUserDetailsService implements UserDetailsService {
    private OurUserRepo ourUserRepo;
    private UserService userService;

    @Autowired
    public OurUserInfoUserDetailsService(OurUserRepo ourUserRepo, UserService userService) {
        this.ourUserRepo = ourUserRepo;
        this.userService = userService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        OurUser user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new OurUserInfoDetails(user);
    }
}