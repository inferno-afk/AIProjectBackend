package com.vasant.AIProjectBackend.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
public class LoginPage {

    @GetMapping("/log")
    public String greet(){
        return "Welcome to the AI Project Backend Login Page!";
    }
}
