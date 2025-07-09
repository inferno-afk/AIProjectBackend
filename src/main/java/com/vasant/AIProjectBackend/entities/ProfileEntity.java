package com.vasant.AIProjectBackend.entities;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "profile_collection")
public class ProfileEntity {
    // getters and setters
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;

    @Indexed(unique = true)
    private String userId;

    @Indexed(unique = true)
    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    @Indexed(unique = true)
    private String email;

    private String verifyOtp;
    private Boolean isAccountVerified;
    private Long verifyOtpExpiryAt;
    private String resetOtp;
    private Long resetOtpExpiryAt;

    @CreatedDate
//    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
