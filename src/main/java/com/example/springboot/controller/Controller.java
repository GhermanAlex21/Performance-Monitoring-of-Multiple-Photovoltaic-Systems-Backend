package com.example.springboot.controller;

import com.example.springboot.config.JwtUtil;
import com.example.springboot.model.OurUser;
import com.example.springboot.model.ResponseMessage;
import com.example.springboot.repository.OurUserRepo;
import com.example.springboot.service.FirebaseService;
import com.example.springboot.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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




    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/user-save")
    public ResponseEntity<?> saveUser(@RequestBody OurUser ourUser) {
        // Verifică dacă username-ul există deja
        boolean userExists = ourUserRepo.findByUsername(ourUser.getUsername()).isPresent();
        if (userExists) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Username already exists"));
        }

        // Setează rolul implicit dacă nu este specificat
        if (ourUser.getRoles() == null || ourUser.getRoles().isEmpty()) {
            ourUser.setRoles("USER");
        }

        // Encodează parola
        ourUser.setPassword(passwordEncoder.encode(ourUser.getPassword()));

        // Salvează utilizatorul în MySQL
        try {
            OurUser savedUser = ourUserRepo.save(ourUser);
            // După salvarea în MySQL, încearcă să salvezi utilizatorul și în Firebase
            String firebaseResult = firebaseService.addUser(savedUser);
            return ResponseEntity.ok(new ResponseMessage("User was saved successfully in MySQL and Firebase at: " + firebaseResult));
        } catch (Exception e) {
            // Tratează excepția specifică și returnează un răspuns corespunzător
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("An error occurred: " + e.getMessage()));
        }
    }


    @GetMapping("/users-all") // Endpoint pentru a obține toți utilizatorii
    public ResponseEntity<List<OurUser>> getAllUsers() {
        try {
            List<OurUser> users = firebaseService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/delete_user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            OurUser user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            // Prima dată ștergem utilizatorul din Firebase
            firebaseService.deleteUser(user.getId().toString());
            // Apoi ștergem utilizatorul din baza de date locală
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully from both MySQL and Firebase.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<OurUser> getUserById(@PathVariable Integer id) {
        try {
            OurUser user = firebaseService.getUserById(id.toString());
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update_user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody OurUser userDetails) {
        try {
            // Obținerea utilizatorului curent pentru a păstra parola veche dacă este necesar
            OurUser existingUser = userService.getUserById(id);
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Actualizează parola doar dacă este furnizată
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            // Actualizează celelalte câmpuri
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setRoles(userDetails.getRoles());

            // Actualizează utilizatorul în baza de date MySQL
            OurUser updatedUser = userService.updateUser(id, existingUser);

            // Actualizează utilizatorul și în Firebase
            String firebaseUpdateResult = firebaseService.updateUser(existingUser);
            return ResponseEntity.ok("User updated successfully in MySQL and Firebase at: " + firebaseUpdateResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody OurUser loginRequest) {
        OurUser user = userService.getUserByUsername(loginRequest.getUsername());
        if (user != null && passwordMatches(loginRequest.getPassword(), user.getPassword())) {
            String token = JwtUtil.generateToken(user.getUsername(), user.getRoles(), user.getId()); // Include ID-ul aici
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
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