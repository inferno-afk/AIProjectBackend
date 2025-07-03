package com.vasant.AIProjectBackend.services;


import com.vasant.AIProjectBackend.entities.User;
import com.vasant.AIProjectBackend.repositories.UserEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserEntryService {

    @Autowired
    private UserEntryRepository userEntryRepository;

    public void signup(User user) {
        try{
            userEntryRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean signin(String email, String password) {
        User user = userEntryRepository.findByEmailAndPassword(email, password);
        log.info("User found: {}", user);
        if (user != null && user.getPassword().equals(password) && user.getEmail().equals(email)) {
            return true; // Sign-in successful
        }

        return false; // Sign-in failed
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
