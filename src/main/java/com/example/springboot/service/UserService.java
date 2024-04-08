package com.example.springboot.service;

import com.example.springboot.model.OurUser;
import com.example.springboot.model.Serie;
import com.example.springboot.repository.OurUserRepo;
import com.example.springboot.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public OurUser getUserByEmail(String email) {
        return ourUserRepo.findByEmail(email).orElse(null);
    }


}
