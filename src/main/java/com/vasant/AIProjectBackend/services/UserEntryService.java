package com.vasant.AIProjectBackend.services;


import com.vasant.AIProjectBackend.entities.User;
import com.vasant.AIProjectBackend.repositories.UserEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntryService {

    @Autowired
    private UserEntryRepository userEntryRepository;

    public void saveUser(User user) {
        try{
            userEntryRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<User> getAllUsers() {
        return userEntryRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userEntryRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
