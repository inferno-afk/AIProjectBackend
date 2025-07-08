package com.vasant.AIProjectBackend.controllers;

import com.vasant.AIProjectBackend.entities.User;
import com.vasant.AIProjectBackend.io.ProfileRequest;
import com.vasant.AIProjectBackend.io.ProfileResponse;
import com.vasant.AIProjectBackend.services.EmailService;
import com.vasant.AIProjectBackend.services.UserEntryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
public class UserEntryController {

    @Autowired
    private UserEntryService userEntryService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/greet")
    public String greet(){
        return "Welcome to the AI Project Backend Login Page!";
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            userEntryService.signup(user);
            response.put("message", "User created successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", "Failed to create user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllUsers")
    public Iterable<User> getAllUsers() {
        return userEntryService.getAllUsers();
    }

    @GetMapping("/getUser/{username}/{password}")
    public ResponseEntity<Map<String, String>> getUser(@PathVariable String username, @PathVariable String password) {
        Map<String, String> response = new HashMap<>();
        User userResponse = userEntryService.getUserByUsername(username);
        if (userResponse == null) {
            response.put("message", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (!Objects.equals(password, userResponse.getPassword())){
            response.put("message", "Incorrect password");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        response.put("message", "Welcome back " + userResponse.getUsername() + "!");
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/signin/{email}/{password}")
    public ResponseEntity<Map<String, String>> signin(@PathVariable String email, @PathVariable String password) {
        boolean isSignedIn = userEntryService.signin(email, password);
        Map<String, String> response = new HashMap<>();
        if (isSignedIn) {
            response.put("message", "Sign-in successful");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } else {
            response.put("message", "Sign-in failed: Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest profileRequest) {
        ProfileResponse profileResponse = userEntryService.createProfile(profileRequest);
        emailService.sendWelcomeEmail(profileResponse.getEmail(), profileResponse.getName());
        return profileResponse;
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return userEntryService.getProfile(email);
    }
}
