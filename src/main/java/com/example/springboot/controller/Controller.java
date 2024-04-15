package com.example.springboot.controller;

import com.example.springboot.config.JwtUtil;
import com.example.springboot.model.OurUser;
import com.example.springboot.model.ResponseMessage;
import com.example.springboot.repository.OurUserRepo;
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

        // Salvează utilizatorul
        try {
            OurUser savedUser = ourUserRepo.save(ourUser);
            if (savedUser != null && savedUser.getId() != null) {
                return ResponseEntity.ok(new ResponseMessage("User was saved successfully."));
            } else {
                return ResponseEntity.badRequest().body(new ResponseMessage("User could not be saved"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("An error occurred while saving the user"));
        }
    }
    @GetMapping("/my-profile")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7); // Remove Bearer prefix
        Integer userId = (Integer) Jwts.parserBuilder()
                .setSigningKey(JwtUtil.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Integer.class); // Extract user ID from JWT

        OurUser user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
    @PutMapping("/update-my-profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody OurUser userDetails, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName(); // Preia numele de utilizator din autentificare
        return ourUserRepo.findByUsername(username)
                .map(user -> {
                    // Actualizează doar câmpurile permise
                    if (userDetails.getNume() != null) user.setNume(userDetails.getNume());
                    if (userDetails.getPrenume() != null) user.setPrenume(userDetails.getPrenume());
                    if (userDetails.getUsername() != null) user.setUsername(userDetails.getUsername());
                    if (userDetails.getTelefon() != null) user.setTelefon(userDetails.getTelefon());
                    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }
                    ourUserRepo.save(user);
                    return ResponseEntity.ok(new ResponseMessage("Profile updated successfully."));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    @GetMapping("/users-all") // Endpoint pentru a obține toate mărcile
    public List<OurUser> getAllUsers() {
        return userService.getAllUsers();
    }


    @DeleteMapping("/delete_user/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<OurUser> getUserById(@PathVariable Integer id) {
        OurUser user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update_user/{id}")
    public ResponseEntity<OurUser> updateUser(@PathVariable Integer id, @RequestBody OurUser userDetails) {
        try {
            // Obținerea utilizatorului curent pentru a păstra parola veche dacă este necesar
            OurUser existingUser = userService.getUserById(id);
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Setează noua parolă doar dacă este furnizată
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            } else {
                // Păstrează parola existentă
                userDetails.setPassword(existingUser.getPassword());
            }

            // Actualizează celelalte câmpuri
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setRoles(userDetails.getRoles());

            // Salvează utilizatorul actualizat
            OurUser updatedUser = userService.updateUser(id, existingUser);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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