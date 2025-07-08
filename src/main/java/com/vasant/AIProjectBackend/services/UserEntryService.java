package com.vasant.AIProjectBackend.services;


import com.vasant.AIProjectBackend.entities.ProfileEntity;
import com.vasant.AIProjectBackend.entities.User;
import com.vasant.AIProjectBackend.io.ProfileRequest;
import com.vasant.AIProjectBackend.io.ProfileResponse;
import com.vasant.AIProjectBackend.repositories.ProfileEntryRepo;
import com.vasant.AIProjectBackend.repositories.UserEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.stringtemplate.v4.ST;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEntryService {

    @Autowired
    private UserEntryRepository userEntryRepository;

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    private ProfileEntryRepo profileEntryRepo;

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

    public ProfileResponse createProfile(ProfileRequest profileRequest){
        if(!profileEntryRepo.existsByEmail(profileRequest.getEmail())){
            ProfileEntity profile = convertToUser(profileRequest);
            profile = profileEntryRepo.save(profile);
            return convertToProfileResponse(profile);
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists: " + profileRequest.getEmail());
    }

    public ProfileResponse getProfile(String email){
        ProfileEntity profileEntity = profileEntryRepo.findByEmail(email).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found for email: " + email));
        return convertToProfileResponse(profileEntity);
    }

    public void sendResetOtp(String email){
        ProfileEntity profile = profileEntryRepo.findByEmail(email).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found for email: " + email));
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,999999));

        long expiryTime = System.currentTimeMillis()+ 5 * 60 * 1000; // OTP valid for 5 minutes

        profile.setResetOtp(otp);
        profile.setResetOtpExpiryAt(expiryTime);

        profileEntryRepo.save(profile);

        try{
            // Assuming an email service is available to send the OTP
             emailService.sendResetOtpEmail(profile.getEmail(), otp, profile.getUsername());
            log.info("OTP sent to {}: {}", email, otp);
        } catch (Exception e) {
            log.error("Failed to send OTP to {}: {}", email, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send OTP");
        }
    }

    public void resetPassword(String email, String otp, String newPassword) {
        ProfileEntity profile = profileEntryRepo.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found for email: " + email));

        if (profile.getResetOtp() == null || !profile.getResetOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        if (System.currentTimeMillis() > profile.getResetOtpExpiryAt()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired");
        }

        profile.setPassword(passwordEncoder.encode(newPassword));
        profile.setResetOtp(null);
        profile.setResetOtpExpiryAt(0L);

        profileEntryRepo.save(profile);
    }

    public void sendOtp(String email){
        ProfileEntity profile = profileEntryRepo.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found for email: " + email));

        if(profile.getIsAccountVerified()!=null && profile.getIsAccountVerified()){
            return;
        }

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        long expiryTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000; // OTP valid for 24 hours

        profile.setVerifyOtp(otp);
        profile.setVerifyOtpExpiryAt(expiryTime);
        profileEntryRepo.save(profile);

        try {
            emailService.sendOtpEmail(profile.getEmail(), otp, profile.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("Unable to send OTP email ");
        }
    }

    public void verifyOtp(String email, String otp){
        ProfileEntity profile = profileEntryRepo.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found for email: " + email));
        if (profile.getVerifyOtp() == null || !profile.getVerifyOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        if (System.currentTimeMillis() > profile.getVerifyOtpExpiryAt()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired");
        }
        profile.setIsAccountVerified(true);
        profile.setVerifyOtp(null);
        profile.setVerifyOtpExpiryAt(0L);

        profileEntryRepo.save(profile);
    }

    private ProfileResponse convertToProfileResponse(ProfileEntity profile) {
        return ProfileResponse.builder()
                .email(profile.getEmail())
                .name(profile.getUsername())
                .userId(profile.getUserId())
                .isAccountVerified(profile.getIsAccountVerified())
                .build();
    }

    private ProfileEntity convertToUser(ProfileRequest profileRequest) {
        return ProfileEntity.builder().email(profileRequest.getEmail())
                .userId(UUID.randomUUID().toString())
                .username(profileRequest.getName())
                .password(passwordEncoder.encode(profileRequest.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpiryAt(0L)
                .verifyOtp(null)
                .verifyOtpExpiryAt(0L)
                .resetOtp(null)
                .build();
    }


}
