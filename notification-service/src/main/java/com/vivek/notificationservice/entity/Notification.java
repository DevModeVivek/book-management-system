package com.vivek.notificationservice.entity;

import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.entity.base.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Notification entity extending BaseEntity with comprehensive validation
 * Follows proper inheritance pattern with Lombok SuperBuilder integration
 */
@Entity
@Table(name = AppConstants.Database.NOTIFICATIONS_TABLE,
       indexes = {
           @Index(name = "idx_notification_recipient", columnList = "recipient_email"),
           @Index(name = "idx_notification_type", columnList = "notification_type"),
           @Index(name = "idx_notification_status", columnList = "status"),
           @Index(name = "idx_notification_active", columnList = AppConstants.Database.IS_ACTIVE_COLUMN)
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true, exclude = {"content"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Notification extends BaseEntity {
    
    @Column(name = "recipient_email", nullable = false, length = 100)
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed {max} characters")
    private String recipientEmail;
    
    @Column(name = "recipient_name", length = 100)
    @Size(max = 100, message = "Recipient name cannot exceed {max} characters")
    private String recipientName;
    
    @Column(name = "subject", nullable = false, length = 200)
    @NotBlank(message = "Subject is required")
    @Size(min = 1, max = 200, message = "Subject must be between {min} and {max} characters")
    private String subject;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 5000, message = "Content must be between {min} and {max} characters")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "retry_count")
    @Min(value = 0, message = "Retry count cannot be negative")
    @Builder.Default
    private Integer retryCount = 0;
    
    @Column(name = "error_message", length = 500)
    @Size(max = 500, message = "Error message cannot exceed {max} characters")
    private String errorMessage;
    
    @Column(name = "template_name", length = 100)
    @Size(max = 100, message = "Template name cannot exceed {max} characters")
    private String templateName;
    
    @Column(name = "reference_id", length = 100)
    @Size(max = 100, message = "Reference ID cannot exceed {max} characters")
    private String referenceId;
    
    @Column(name = "reference_type", length = 50)
    @Size(max = 50, message = "Reference type cannot exceed {max} characters")
    private String referenceType;
    
    // ============= BUSINESS METHODS =============
    
    /**
     * Check if notification is pending
     */
    public boolean isPending() {
        return NotificationStatus.PENDING.equals(this.status);
    }
    
    /**
     * Check if notification is sent successfully
     */
    public boolean isSent() {
        return NotificationStatus.SENT.equals(this.status);
    }
    
    /**
     * Check if notification failed
     */
    public boolean isFailed() {
        return NotificationStatus.FAILED.equals(this.status);
    }
    
    /**
     * Mark notification as sent
     */
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.errorMessage = null;
    }
    
    /**
     * Mark notification as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount++;
    }
    
    /**
     * Reset for retry
     */
    public void resetForRetry() {
        this.status = NotificationStatus.PENDING;
        this.errorMessage = null;
    }
    
    /**
     * Check if notification can be retried
     */
    public boolean canRetry() {
        return this.retryCount < 3 && (isFailed() || isPending());
    }
    
    /**
     * Get full recipient display
     */
    public String getRecipientDisplay() {
        if (recipientName != null && !recipientName.trim().isEmpty()) {
            return String.format("%s <%s>", recipientName, recipientEmail);
        }
        return recipientEmail;
    }
    
    /**
     * Check if notification has reference
     */
    public boolean hasReference() {
        return referenceId != null && referenceType != null;
    }
    
    @Override
    public String toLogString() {
        return String.format("Notification{id=%s, recipient='%s', type=%s, status=%s, active=%s}", 
                getId(), recipientEmail, notificationType, status, isActive());
    }
    
    // ============= VALIDATION HELPER METHODS =============
    
    /**
     * Validate business rules for the notification
     */
    public void validateBusinessRules() {
        if (notificationType == null) {
            throw new IllegalStateException("Notification type cannot be null");
        }
        
        if (status == null) {
            throw new IllegalStateException("Status cannot be null");
        }
        
        if (retryCount != null && retryCount < 0) {
            throw new IllegalStateException("Retry count cannot be negative");
        }
    }
    
    @PrePersist
    @PreUpdate
    protected void validateEntity() {
        validateBusinessRules();
        super.prePersist();
    }
    
    // ============= NOTIFICATION TYPE ENUM =============
    
    public enum NotificationType {
        WELCOME("Welcome Email"),
        BOOK_CREATED("Book Created Notification"),
        USER_REGISTERED("User Registration Confirmation"),
        PASSWORD_RESET("Password Reset"),
        ACCOUNT_LOCKED("Account Locked Notice"),
        SYSTEM_ALERT("System Alert");
        
        private final String displayName;
        
        NotificationType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // ============= NOTIFICATION STATUS ENUM =============
    
    public enum NotificationStatus {
        PENDING("Pending"),
        SENT("Sent"),
        FAILED("Failed");
        
        private final String displayName;
        
        NotificationStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}