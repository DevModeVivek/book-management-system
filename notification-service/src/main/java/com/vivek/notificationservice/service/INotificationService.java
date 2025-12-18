package com.vivek.notificationservice.service;

import com.vivek.notificationservice.dto.NotificationDTO;
import com.vivek.notificationservice.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Notification Service Interface
 * Service for handling notifications
 */
public interface INotificationService {
    
    /**
     * Send a notification
     * @param notification The notification to send
     * @return The sent notification DTO
     */
    NotificationDTO sendNotification(Notification notification);
    
    /**
     * Send a notification from DTO
     * @param notificationDTO The notification DTO to send
     * @return The sent notification DTO
     */
    NotificationDTO sendNotification(NotificationDTO notificationDTO);
    
    /**
     * Get all notifications for a recipient
     * @param recipientEmail The recipient's email
     * @return List of notifications
     */
    List<NotificationDTO> getNotificationsForRecipient(String recipientEmail);
    
    /**
     * Get notifications with pagination
     * @param pageable Pagination information
     * @return Page of notifications
     */
    Page<NotificationDTO> getAllNotifications(Pageable pageable);
    
    /**
     * Get notification by ID
     * @param id The notification ID
     * @return Optional notification DTO
     */
    Optional<NotificationDTO> getNotificationById(Long id);
    
    /**
     * Get notifications by status
     * @param status The notification status
     * @return List of notifications with the given status
     */
    List<NotificationDTO> getNotificationsByStatus(Notification.NotificationStatus status);
    
    /**
     * Retry failed notifications
     * @return Number of notifications retried
     */
    int retryFailedNotifications();
    
    /**
     * Mark notification as read
     * @param id The notification ID
     */
    void markAsRead(Long id);
    
    /**
     * Delete notification
     * @param id The notification ID
     */
    void deleteNotification(Long id);
}