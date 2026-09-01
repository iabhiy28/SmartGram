package com.gramconnect.modules.auth.service;

import com.gramconnect.common.exception.BadRequestException;
import com.gramconnect.common.exception.ConflictException;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.common.exception.UnauthorizedException;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.common.security.JwtTokenProvider;
import com.gramconnect.modules.auth.dto.*;
import com.gramconnect.modules.auth.entity.RefreshToken;
import com.gramconnect.modules.auth.repository.RefreshTokenRepository;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${gramconnect.jwt.access-token-expiration-ms:900000}")
    private long accessTokenExpirationMs;

    @Value("${gramconnect.jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    /**
     * Registers a new user with BCrypt password hashing and issues an initial JWT session.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ConflictException("Phone number is already registered: " + request.getPhoneNumber());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email is already registered: " + request.getEmail());
            }
        }

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .villageId(request.getVillageId())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .occupation(request.getOccupation())
                .annualIncome(request.getAnnualIncome())
                .casteCategory(request.getCasteCategory())
                .landOwnership(Boolean.TRUE.equals(request.getLandOwnership()))
                .aadhaarLastFour(request.getAadhaarLastFour())
                .languagePreference(request.getLanguagePreference() != null ? request.getLanguagePreference() : "en")
                .isActive(true)
                .isVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user [ID: {}, Role: {}]", savedUser.getId(), savedUser.getRole());

        return createAuthSession(savedUser, "Initial Registration");
    }

    /**
     * Authenticates a user by phone and password.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new UnauthorizedException("Invalid phone number or password"));

        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account has been deactivated. Please contact your Panchayat Administrator.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid phone number or password");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        log.info("User authenticated successfully [ID: {}]", user.getId());
        return createAuthSession(user, request.getDeviceInfo());
    }

    /**
     * Refresh Token Rotation (RTR): Validates and consumes the submitted refresh token,
     * invalidates it immediately, and issues a fresh Access Token + Refresh Token pair.
     */
    @Transactional
    public AuthResponse refreshAccessToken(TokenRefreshRequest request) {
        String rawRefreshToken = request.getRefreshToken();
        String tokenHash = JwtTokenProvider.hashToken(rawRefreshToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid or unrecognized refresh token"));

        // Breach Detection: If a revoked token is presented, revoke ALL tokens for that user
        if (refreshToken.getIsRevoked()) {
            log.warn("Breach detected! Revoked refresh token reuse attempt for user: {}", refreshToken.getUser().getId());
            refreshTokenRepository.revokeAllTokensForUser(refreshToken.getUser());
            throw new UnauthorizedException("Session revoked due to security breach detection. Please log in again.");
        }

        if (refreshToken.isExpired()) {
            refreshToken.setIsRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new UnauthorizedException("Refresh token has expired. Please log in again.");
        }

        // Invalidate current refresh token (Rotation)
        refreshToken.setIsRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is no longer active.");
        }

        log.info("Rotated refresh token for user [ID: {}]", user.getId());
        return createAuthSession(user, request.getDeviceInfo());
    }

    /**
     * Revokes a refresh token on user logout.
     */
    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            String tokenHash = JwtTokenProvider.hashToken(rawRefreshToken);
            refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
                token.setIsRevoked(true);
                refreshTokenRepository.save(token);
                log.info("Revoked refresh token on logout for user [ID: {}]", token.getUser().getId());
            });
        }
    }

    /**
     * Changes user password after validating current password.
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Invalidate all existing sessions on password change
        refreshTokenRepository.revokeAllTokensForUser(user);
        log.info("Password changed and sessions revoked for user [ID: {}]", userId);
    }

    /**
     * Helper to generate Access Token and persist SHA-256 hashed Refresh Token.
     */
    private AuthResponse createAuthSession(User user, String deviceInfo) {
        CustomUserDetails userDetails = CustomUserDetails.build(user);
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

        // Generate high-entropy raw refresh token
        String rawRefreshToken = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        String tokenHash = JwtTokenProvider.hashToken(rawRefreshToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .deviceInfo(deviceInfo)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .tokenType("Bearer")
                .expiresInMs(accessTokenExpirationMs)
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .role(user.getRole())
                .villageId(user.getVillageId())
                .languagePreference(user.getLanguagePreference())
                .build();
    }
}
