package com.vivek.notificationservice.repository;

import com.vivek.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity operations
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find notifications by recipient email
     */
    List<Notification> findByRecipientEmailAndIsActiveTrue(String recipientEmail);
    
    /**
     * Find notifications by status
     */
    List<Notification> findByStatusAndIsActiveTrue(Notification.NotificationStatus status);
    
    /**
     * Find notifications by type
     */
    List<Notification> findByNotificationTypeAndIsActiveTrue(Notification.NotificationType notificationType);
    
    /**
     * Find pending notifications that can be retried
     */
    @Query("SELECT n FROM Notification n WHERE " +
           "n.status = 'PENDING' AND n.retryCount < 3 AND n.isActive = true")
    List<Notification> findRetryableNotifications();
    
    /**
     * Find failed notifications within time range
     */
    @Query("SELECT n FROM Notification n WHERE " +
           "n.status = 'FAILED' AND n.updatedAt BETWEEN :startTime AND :endTime AND n.isActive = true")
    List<Notification> findFailedNotificationsBetween(@Param("startTime") LocalDateTime startTime, 
                                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * Find notifications by reference
     */
    List<Notification> findByReferenceIdAndReferenceTypeAndIsActiveTrue(String referenceId, String referenceType);
}