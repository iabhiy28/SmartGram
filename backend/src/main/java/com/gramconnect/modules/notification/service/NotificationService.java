package com.gramconnect.modules.notification.service;

import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.notification.dto.NotificationResponse;
import com.gramconnect.modules.notification.entity.Notification;
import com.gramconnect.modules.notification.repository.NotificationRepository;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Notification Service — creates, queries, and manages in-app notifications.
 * Other services call createNotification() to send notifications to users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    /**
     * Create and persist a notification for a specific user, and push it in real-time over STOMP.
     */
    @Transactional
    public void createNotification(UUID userId, String title, String message,
                                    String notificationType, String referenceType, UUID referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = NotificationResponse.fromEntity(saved);

        try {
            messagingTemplate.convertAndSend("/topic/user." + userId + ".notifications", response);
        } catch (Exception e) {
            log.warn("Failed to deliver real-time WebSocket notification to user {}: {}", userId, e.getMessage());
        }

        log.debug("Notification created for user [ID: {}]: {}", userId, title);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getNotifications(UUID userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(NotificationResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getUnreadNotifications(UUID userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(NotificationResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new com.gramconnect.common.exception.ForbiddenException("You cannot modify this notification");
        }

        notification.setIsRead(true);
        notification.setReadAt(Instant.now());
        return NotificationResponse.fromEntity(notificationRepository.save(notification));
    }

    @Transactional
    public int markAllAsRead(UUID userId) {
        int count = notificationRepository.markAllAsRead(userId, Instant.now());
        log.info("Marked {} notifications as read for user [ID: {}]", count, userId);
        return count;
    }
}
