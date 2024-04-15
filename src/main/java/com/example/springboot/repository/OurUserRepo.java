package com.example.springboot.repository;

import com.example.springboot.model.OurUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import com.example.springboot.model.OurUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OurUserRepo extends JpaRepository<OurUser, Integer> {

    Optional<OurUser> findByUsername(String username);
}