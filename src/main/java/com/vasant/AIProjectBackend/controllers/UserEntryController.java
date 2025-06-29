package com.vasant.AIProjectBackend.controllers;

import com.vasant.AIProjectBackend.entities.User;
import com.vasant.AIProjectBackend.services.UserEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
public class UserEntryController {

    @Autowired
    private UserEntryService userEntryService;

    @GetMapping("/greet")
    public String greet(){
        return "Welcome to the AI Project Backend Login Page!";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            userEntryService.saveUser(user);
            response.put("message", "User created successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", "Failed to create user: " + e.getMessage());
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
}
