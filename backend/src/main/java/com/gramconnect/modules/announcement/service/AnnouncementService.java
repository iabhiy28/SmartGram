package com.gramconnect.modules.announcement.service;

import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.exception.ForbiddenException;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.announcement.dto.*;
import com.gramconnect.modules.announcement.entity.Announcement;
import com.gramconnect.modules.announcement.entity.EmergencyContact;
import com.gramconnect.modules.announcement.repository.AnnouncementRepository;
import com.gramconnect.modules.announcement.repository.EmergencyContactRepository;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.hierarchy.repository.VillageRepository;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final UserRepository userRepository;
    private final VillageRepository villageRepository;

    // ========================================================================
    // ANNOUNCEMENTS
    // ========================================================================

    @Transactional
    public AnnouncementResponse createAnnouncement(UUID authorId, CreateAnnouncementRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));

        Village village = null;
        if (request.getVillageId() != null) {
            village = villageRepository.findById(request.getVillageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Village", "id", request.getVillageId()));
        }

        Announcement announcement = Announcement.builder()
                .author(author)
                .village(village)
                .title(request.getTitle())
                .content(request.getContent())
                .announcementType(request.getAnnouncementType() != null ? request.getAnnouncementType() : "GENERAL")
                .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
                .attachmentUrl(request.getAttachmentUrl())
                .expiresAt(request.getExpiresAt())
                .isActive(true)
                .viewCount(0)
                .build();

        Announcement saved = announcementRepository.save(announcement);
        log.info("Announcement created [ID: {}, Title: '{}'] by author [ID: {}]",
                saved.getId(), saved.getTitle(), authorId);
        return AnnouncementResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public AnnouncementResponse getAnnouncementById(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        return AnnouncementResponse.fromEntity(announcement);
    }

    @Transactional(readOnly = true)
    public PageResponse<AnnouncementResponse> getVillageAnnouncements(UUID villageId, Pageable pageable) {
        Page<Announcement> page = announcementRepository.findActiveAnnouncementsForVillage(villageId, Instant.now(), pageable);
        return PageResponse.from(page.map(AnnouncementResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<AnnouncementResponse> getMyAnnouncements(UUID authorId, Pageable pageable) {
        Page<Announcement> page = announcementRepository.findByAuthorId(authorId, pageable);
        return PageResponse.from(page.map(AnnouncementResponse::fromEntity));
    }

    @Transactional
    public void deactivateAnnouncement(UUID announcementId, UUID actorId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", announcementId));

        if (!announcement.getAuthor().getId().equals(actorId)) {
            throw new ForbiddenException("Only the author can deactivate this announcement");
        }

        announcement.setIsActive(false);
        announcementRepository.save(announcement);
        log.info("Announcement deactivated [ID: {}] by user [ID: {}]", announcementId, actorId);
    }

    @Transactional
    public AnnouncementResponse incrementViewCount(UUID announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", announcementId));
        announcement.setViewCount(announcement.getViewCount() + 1);
        return AnnouncementResponse.fromEntity(announcementRepository.save(announcement));
    }

    // ========================================================================
    // EMERGENCY CONTACTS
    // ========================================================================

    @Transactional(readOnly = true)
    public List<EmergencyContactResponse> getEmergencyContacts(UUID villageId) {
        List<EmergencyContact> contacts;
        if (villageId != null) {
            contacts = emergencyContactRepository.findActiveContactsForVillage(villageId);
        } else {
            contacts = emergencyContactRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        }
        return contacts.stream().map(EmergencyContactResponse::fromEntity).toList();
    }
}
