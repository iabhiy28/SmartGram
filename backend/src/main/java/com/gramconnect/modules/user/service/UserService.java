package com.gramconnect.modules.user.service;

import com.gramconnect.common.exception.ConflictException;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.user.dto.UpdateProfileRequest;
import com.gramconnect.modules.user.dto.UserProfileResponse;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return UserProfileResponse.fromEntity(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email is already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getVillageId() != null) user.setVillageId(request.getVillageId());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getOccupation() != null) user.setOccupation(request.getOccupation());
        if (request.getAnnualIncome() != null) user.setAnnualIncome(request.getAnnualIncome());
        if (request.getCasteCategory() != null) user.setCasteCategory(request.getCasteCategory());
        if (request.getLandOwnership() != null) user.setLandOwnership(request.getLandOwnership());
        if (request.getLanguagePreference() != null) user.setLanguagePreference(request.getLanguagePreference());
        if (request.getBio() != null) user.setBio(request.getBio());

        User updatedUser = userRepository.save(user);
        log.info("Updated profile for user [ID: {}]", userId);
        return UserProfileResponse.fromEntity(updatedUser);
    }
}
