package com.gramconnect.modules.announcement.repository;

import com.gramconnect.modules.announcement.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {

    @Query("""
            SELECT a FROM Announcement a
            WHERE a.isActive = true
              AND (a.expiresAt IS NULL OR a.expiresAt > :now)
              AND (a.village.id = :villageId OR a.village IS NULL)
            ORDER BY a.priority DESC, a.createdAt DESC
            """)
    Page<Announcement> findActiveAnnouncementsForVillage(
            @Param("villageId") UUID villageId,
            @Param("now") Instant now,
            Pageable pageable);

    /** Platform-wide announcements (village IS NULL) */
    @Query("""
            SELECT a FROM Announcement a
            WHERE a.isActive = true
              AND a.village IS NULL
              AND (a.expiresAt IS NULL OR a.expiresAt > :now)
            ORDER BY a.createdAt DESC
            """)
    Page<Announcement> findPlatformAnnouncements(@Param("now") Instant now, Pageable pageable);

    Page<Announcement> findByAuthorId(UUID authorId, Pageable pageable);
}
