package com.example.springboot.controller;

import com.example.springboot.config.JwtUtil;
import com.example.springboot.model.OurUser;
import com.example.springboot.repository.OurUserRepo;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class Controller {
    @Autowired
    private OurUserRepo ourUserRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;


    @GetMapping("/")
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public String home() {
        return "home";
    }




    @PostMapping("/user-save")
    public ResponseEntity<Object> saveUSer(@RequestBody OurUser ourUser){
        ourUser.setPassword(passwordEncoder.encode(ourUser.getPassword()));
        OurUser result = ourUserRepo.save(ourUser);
        if (result.getId() > 0){
            return ResponseEntity.ok("USer Was Saved");
        }
        return ResponseEntity.status(404).body("Error, USer Not Saved");
    }
    @GetMapping("/users-all") // Endpoint pentru a obține toate mărcile
    public List<OurUser> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/users-single")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Object> getMyDetails(){
        return ResponseEntity.ok(ourUserRepo.findByEmail(getLoggedInUserDetails().getUsername()));
    }

    @DeleteMapping("/delete_user/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody OurUser loginRequest) {
        OurUser user = userService.getUserByEmail(loginRequest.getEmail());
        if (user != null && passwordMatches(loginRequest.getPassword(), user.getPassword())) {
            String token = JwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
    }
    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }




    public UserDetails getLoggedInUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails){
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }
}