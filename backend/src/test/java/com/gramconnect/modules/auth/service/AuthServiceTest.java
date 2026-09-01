package com.gramconnect.modules.auth.service;

import com.gramconnect.common.exception.ConflictException;
import com.gramconnect.common.exception.UnauthorizedException;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.common.security.JwtTokenProvider;
import com.gramconnect.modules.auth.dto.AuthResponse;
import com.gramconnect.modules.auth.dto.LoginRequest;
import com.gramconnect.modules.auth.dto.RegisterRequest;
import com.gramconnect.modules.auth.entity.RefreshToken;
import com.gramconnect.modules.auth.repository.RefreshTokenRepository;
import com.gramconnect.modules.user.entity.Role;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;
    private UUID villageId;

    @BeforeEach
    void setUp() {
        villageId = UUID.randomUUID();
        ReflectionTestUtils.setField(authService, "accessTokenExpirationMs", 900000L);
        ReflectionTestUtils.setField(authService, "refreshTokenExpirationMs", 604800000L);

        sampleUser = User.builder()
                .phoneNumber("9876543210")
                .passwordHash("$2a$12$hashedPasswordExample")
                .fullName("Ramesh Kumar")
                .role(Role.ROLE_VILLAGER)
                .villageId(villageId)
                .isActive(true)
                .build();
        sampleUser.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void register_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .phoneNumber("9876543210")
                .password("StrongPassword123")
                .fullName("Ramesh Kumar")
                .role(Role.ROLE_VILLAGER)
                .villageId(villageId)
                .build();

        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$12$hashedPasswordExample");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(jwtTokenProvider.generateAccessToken(any(CustomUserDetails.class))).thenReturn("mock-jwt-access-token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock-jwt-access-token");
        assertThat(response.getPhoneNumber()).isEqualTo("9876543210");
        verify(userRepository, times(1)).save(any(User.class));
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when registering with an existing phone number")
    void register_DuplicatePhoneNumber_ThrowsConflict() {
        RegisterRequest request = RegisterRequest.builder()
                .phoneNumber("9876543210")
                .password("StrongPassword123")
                .fullName("Ramesh Kumar")
                .build();

        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    @DisplayName("Should successfully authenticate valid credentials")
    void login_Success() {
        LoginRequest request = LoginRequest.builder()
                .phoneNumber("9876543210")
                .password("CorrectPassword")
                .build();

        when(userRepository.findByPhoneNumber(request.getPhoneNumber())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("CorrectPassword", sampleUser.getPasswordHash())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any(CustomUserDetails.class))).thenReturn("mock-jwt-access-token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock-jwt-access-token");
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException on invalid password")
    void login_InvalidPassword_ThrowsUnauthorized() {
        LoginRequest request = LoginRequest.builder()
                .phoneNumber("9876543210")
                .password("WrongPassword")
                .build();

        when(userRepository.findByPhoneNumber(request.getPhoneNumber())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("WrongPassword", sampleUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid phone number or password");
    }
}
