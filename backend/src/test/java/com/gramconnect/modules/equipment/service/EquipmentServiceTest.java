package com.gramconnect.modules.equipment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gramconnect.common.exception.BadRequestException;
import com.gramconnect.common.exception.ConflictException;
import com.gramconnect.modules.equipment.dto.CreateEquipmentBookingRequest;
import com.gramconnect.modules.equipment.dto.EquipmentBookingResponse;
import com.gramconnect.modules.equipment.entity.Equipment;
import com.gramconnect.modules.equipment.entity.EquipmentBooking;
import com.gramconnect.modules.equipment.entity.EquipmentBookingStatus;
import com.gramconnect.modules.equipment.entity.EquipmentCategory;
import com.gramconnect.modules.equipment.repository.EquipmentBookingRepository;
import com.gramconnect.modules.equipment.repository.EquipmentCategoryRepository;
import com.gramconnect.modules.equipment.repository.EquipmentRepository;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.hierarchy.repository.VillageRepository;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentCategoryRepository categoryRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentBookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VillageRepository villageRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EquipmentService equipmentService;

    private User owner;
    private User renter;
    private Equipment tractor;
    private UUID tractorId;
    private UUID ownerId;
    private UUID renterId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        renterId = UUID.randomUUID();
        tractorId = UUID.randomUUID();

        Village village = Village.builder().name("Bidadi").build();
        village.setId(UUID.randomUUID());

        owner = User.builder().fullName("Suresh Patel").build();
        owner.setId(ownerId);

        renter = User.builder().fullName("Gopal Gowda").build();
        renter.setId(renterId);

        EquipmentCategory category = EquipmentCategory.builder().name("TRACTOR").displayName("Tractor").build();

        tractor = Equipment.builder()
                .owner(owner)
                .village(village)
                .category(category)
                .title("Mahindra 575 DI 45HP Tractor")
                .dailyRate(BigDecimal.valueOf(1500))
                .hourlyRate(BigDecimal.valueOf(250))
                .isActive(true)
                .isOperational(true)
                .build();
        tractor.setId(tractorId);
    }

    @Test
    @DisplayName("Should successfully book equipment when no dates overlap")
    void bookEquipment_Success() {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(4);

        CreateEquipmentBookingRequest request = CreateEquipmentBookingRequest.builder()
                .startDate(start)
                .endDate(end)
                .rateType("DAILY")
                .renterNotes("Need for field preparation")
                .build();

        when(equipmentRepository.findEquipmentForUpdate(tractorId)).thenReturn(Optional.of(tractor));
        when(userRepository.findById(renterId)).thenReturn(Optional.of(renter));
        when(bookingRepository.countOverlappingBookings(eq(tractorId), any(), eq(start), eq(end))).thenReturn(0L);
        when(bookingRepository.save(any(EquipmentBooking.class))).thenAnswer(invocation -> {
            EquipmentBooking b = invocation.getArgument(0);
            b.setId(UUID.randomUUID());
            return b;
        });

        EquipmentBookingResponse response = equipmentService.bookEquipment(tractorId, renterId, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(EquipmentBookingStatus.PENDING);
        assertThat(response.getTotalDays()).isEqualTo(3);
        assertThat(response.getTotalAmount()).isEqualTo(BigDecimal.valueOf(4500)); // 3 days * 1500
        verify(bookingRepository, times(1)).save(any(EquipmentBooking.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when requested dates overlap with an existing booking")
    void bookEquipment_DateOverlap_ThrowsConflict() {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(5);

        CreateEquipmentBookingRequest request = CreateEquipmentBookingRequest.builder()
                .startDate(start)
                .endDate(end)
                .rateType("DAILY")
                .build();

        when(equipmentRepository.findEquipmentForUpdate(tractorId)).thenReturn(Optional.of(tractor));
        when(userRepository.findById(renterId)).thenReturn(Optional.of(renter));
        when(bookingRepository.countOverlappingBookings(eq(tractorId), any(), eq(start), eq(end))).thenReturn(1L);

        assertThatThrownBy(() -> equipmentService.bookEquipment(tractorId, renterId, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already booked for the requested dates");
    }

    @Test
    @DisplayName("Should prevent owner from booking their own equipment")
    void bookEquipment_SelfBooking_ThrowsBadRequest() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(2);

        CreateEquipmentBookingRequest request = CreateEquipmentBookingRequest.builder()
                .startDate(start)
                .endDate(end)
                .rateType("DAILY")
                .build();

        when(equipmentRepository.findEquipmentForUpdate(tractorId)).thenReturn(Optional.of(tractor));

        assertThatThrownBy(() -> equipmentService.bookEquipment(tractorId, ownerId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot book your own equipment");
    }
}
