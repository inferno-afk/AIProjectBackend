package com.vasant.AIProjectBackend.repositories;

import com.vasant.AIProjectBackend.entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController

public interface UserEntryRepository extends MongoRepository<User, ObjectId> {
    User findByEmailAndPassword(String email, String password);
}
