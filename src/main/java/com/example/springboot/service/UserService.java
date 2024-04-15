package com.example.springboot.service;

import com.example.springboot.model.OurUser;
import com.example.springboot.model.Serie;
import com.example.springboot.repository.OurUserRepo;
import com.example.springboot.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class UserService {

    @Autowired
    private OurUserRepo ourUserRepo;
    public List<OurUser> getAllUsers() {
        return ourUserRepo.findAll();
    }

    public void deleteUser(Integer id) {
        ourUserRepo.deleteById(id);
    }

    public OurUser getUserByUsername(String username) {
        return ourUserRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public OurUser getUserById(Integer id) {
        return ourUserRepo.findById(id).orElse(null);
    }

    public OurUser updateUser(Integer id, OurUser userDetails) {
        return ourUserRepo.findById(id)
                .map(user -> {
                    user.setUsername(userDetails.getUsername());
                    user.setRoles(userDetails.getRoles());
                    user.setPassword(userDetails.getPassword());
                    return ourUserRepo.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public OurUser updateProfile(Integer id, OurUser userDetails) {
        return ourUserRepo.findById(id)
                .map(user -> {
                    user.setUsername(userDetails.getUsername());
                    user.setPassword(userDetails.getPassword());// Nu ar trebui să fie necesară nicio conversie dacă este deja un string
                    return ourUserRepo.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }


}
