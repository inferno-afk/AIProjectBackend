package com.vasant.AIProjectBackend.repositories;

import com.vasant.AIProjectBackend.entities.ProfileEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ProfileEntryRepo extends MongoRepository<ProfileEntity, ObjectId> {
    Optional<ProfileEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
}
