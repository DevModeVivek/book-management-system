package com.vivek.notificationservice.service.impl;

import com.vivek.commons.constants.AppConstants;
import com.vivek.notificationservice.dto.NotificationDTO;
import com.vivek.notificationservice.entity.Notification;
import com.vivek.notificationservice.repository.NotificationRepository;
import com.vivek.notificationservice.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Notification Service Implementation
 * Handles notification operations
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService implements INotificationService {
    
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public NotificationDTO sendNotification(Notification notification) {
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "NotificationService.sendNotification",
                String.format("Sending notification to: %s", notification.getRecipientEmail()));
        
        try {
            // Set default values
            if (notification.getStatus() == null) {
                notification.setStatus(Notification.NotificationStatus.PENDING);
            }
            if (notification.getRetryCount() == null) {
                notification.setRetryCount(0);
            }
            
            // Validate business rules
            notification.validateBusinessRules();
            
            // Save notification
            Notification savedNotification = notificationRepository.save(notification);
            
            // Simulate sending notification (in real implementation, integrate with email service)
            boolean sendSuccess = simulateNotificationSending(savedNotification);
            
            if (sendSuccess) {
                savedNotification.markAsSent();
                log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_SERVICE,
                        "NotificationService.sendNotification",
                        String.format("Successfully sent notification ID: %d to: %s", 
                                savedNotification.getId(), savedNotification.getRecipientEmail()));
            } else {
                savedNotification.markAsFailed("Simulated sending failure");
                log.warn(AppConstants.Logging.SERVICE_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_SERVICE,
                        "NotificationService.sendNotification",
                        String.format("Failed to send notification ID: %d to: %s", 
                                savedNotification.getId(), savedNotification.getRecipientEmail()));
            }
            
            // Save updated notification
            savedNotification = notificationRepository.save(savedNotification);
            
            return mapToDTO(savedNotification);
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "NotificationService.sendNotification",
                    "NOTIFICATION_SEND_FAILED",
                    e.getMessage());
            
            // Mark as failed and save
            notification.markAsFailed(e.getMessage());
            notificationRepository.save(notification);
            
            throw new RuntimeException("Failed to send notification", e);
        }
    }
    
    @Override
    @Transactional
    public NotificationDTO sendNotification(NotificationDTO notificationDTO) {
        Notification notification = mapToEntity(notificationDTO);
        return sendNotification(notification);
    }
    
    @Override
    public List<NotificationDTO> getNotificationsForRecipient(String recipientEmail) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "NotificationService.getNotificationsForRecipient",
                String.format("Getting notifications for: %s", recipientEmail));
        
        List<Notification> notifications = notificationRepository
                .findByRecipientEmailAndIsActiveTrue(recipientEmail);
        
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<NotificationDTO> getAllNotifications(Pageable pageable) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "NotificationService.getAllNotifications",
                String.format("Getting all notifications - page: %d, size: %d", 
                        pageable.getPageNumber(), pageable.getPageSize()));
        
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(this::mapToDTO);
    }
    
    @Override
    public Optional<NotificationDTO> getNotificationById(Long id) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "NotificationService.getNotificationById",
                String.format("Getting notification by ID: %d", id));
        
        return notificationRepository.findById(id)
                .map(this::mapToDTO);
    }
    
    @Override
    public List<NotificationDTO> getNotificationsByStatus(Notification.NotificationStatus status) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "NotificationService.getNotificationsByStatus",
                String.format("Getting notifications by status: %s", status));
        
        List<Notification> notifications = notificationRepository
                .findByStatusAndIsActiveTrue(status);
        
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public int retryFailedNotifications() {
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "NotificationService.retryFailedNotifications",
                "Starting retry of failed notifications");
        
        List<Notification> retryableNotifications = notificationRepository.findRetryableNotifications();
        int successCount = 0;
        
        for (Notification notification : retryableNotifications) {
            try {
                notification.resetForRetry();
                boolean sendSuccess = simulateNotificationSending(notification);
                
                if (sendSuccess) {
                    notification.markAsSent();
                    successCount++;
                    log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                            AppConstants.Logging.CATEGORY_SERVICE,
                            "NotificationService.retryFailedNotifications",
                            String.format("Successfully retried notification ID: %d", notification.getId()));
                } else {
                    notification.markAsFailed("Retry failed - simulated failure");
                }
                
                notificationRepository.save(notification);
                
            } catch (Exception e) {
                notification.markAsFailed("Retry failed: " + e.getMessage());
                notificationRepository.save(notification);
                
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_SERVICE,
                        "NotificationService.retryFailedNotifications",
                        "RETRY_FAILED",
                        String.format("Failed to retry notification ID: %d - %s", 
                                notification.getId(), e.getMessage()));
            }
        }
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "NotificationService.retryFailedNotifications",
                String.format("Completed retry process. Success: %d/%d", successCount, retryableNotifications.size()));
        
        return successCount;
    }
    
    @Override
    @Transactional
    public void markAsRead(Long id) {
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "NotificationService.markAsRead",
                String.format("Marking notification as read: %d", id));
        
        Optional<Notification> notificationOpt = notificationRepository.findById(id);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            // In a real implementation, you might have a 'read' flag
            // For now, we'll just update the timestamp
            notification.setUpdatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        } else {
            log.warn(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "NotificationService.markAsRead",
                    String.format("Notification not found with ID: %d", id));
        }
    }
    
    @Override
    @Transactional
    public void deleteNotification(Long id) {
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "NotificationService.deleteNotification",
                String.format("Soft deleting notification: %d", id));
        
        Optional<Notification> notificationOpt = notificationRepository.findById(id);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setIsActive(false);
            notification.setUpdatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        } else {
            log.warn(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "NotificationService.deleteNotification",
                    String.format("Notification not found with ID: %d", id));
        }
    }
    
    // ============= PRIVATE HELPER METHODS =============
    
    /**
     * Simulate notification sending (replace with real email service integration)
     */
    private boolean simulateNotificationSending(Notification notification) {
        try {
            // Simulate processing time
            Thread.sleep(100);
            
            // Simulate 90% success rate
            return Math.random() > 0.1;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Map notification entity to DTO
     */
    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = modelMapper.map(notification, NotificationDTO.class);
        
        // Set any additional properties if needed
        if (notification.getCreatedAt() != null) {
            dto.setCreatedAt(notification.getCreatedAt());
        }
        if (notification.getUpdatedAt() != null) {
            dto.setUpdatedAt(notification.getUpdatedAt());
        }
        
        return dto;
    }
    
    /**
     * Map notification DTO to entity
     */
    private Notification mapToEntity(NotificationDTO dto) {
        Notification notification = modelMapper.map(dto, Notification.class);
        
        // Set any additional properties if needed
        if (notification.getStatus() == null) {
            notification.setStatus(Notification.NotificationStatus.PENDING);
        }
        if (notification.getRetryCount() == null) {
            notification.setRetryCount(0);
        }
        
        return notification;
    }
}