package com.vivek.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vivek.commons.dto.base.BaseDTO;
import com.vivek.notificationservice.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Notification Data Transfer Object
 * DTO for notification operations
 */
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true, exclude = {"content"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Notification data transfer object")
public class NotificationDTO extends BaseDTO {
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed {max} characters")
    @JsonProperty("recipientEmail")
    @Schema(description = "Recipient's email address", example = "user@example.com", required = true)
    private String recipientEmail;
    
    @Size(max = 100, message = "Recipient name cannot exceed {max} characters")
    @JsonProperty("recipientName")
    @Schema(description = "Recipient's display name", example = "John Doe")
    private String recipientName;
    
    @NotBlank(message = "Subject is required")
    @Size(min = 1, max = 200, message = "Subject must be between {min} and {max} characters")
    @JsonProperty("subject")
    @Schema(description = "Notification subject", example = "New Book Added", required = true)
    private String subject;
    
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 5000, message = "Content must be between {min} and {max} characters")
    @JsonProperty("content")
    @Schema(description = "Notification content/body", required = true)
    private String content;
    
    @NotNull(message = "Notification type is required")
    @JsonProperty("notificationType")
    @Schema(description = "Type of notification", required = true)
    private Notification.NotificationType notificationType;
    
    @JsonProperty("status")
    @Schema(description = "Notification status", example = "PENDING")
    @Builder.Default
    private Notification.NotificationStatus status = Notification.NotificationStatus.PENDING;
    
    @JsonProperty("sentAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Schema(description = "When the notification was sent", example = "2023-12-14T10:30:00.000")
    private LocalDateTime sentAt;
    
    @Min(value = 0, message = "Retry count cannot be negative")
    @JsonProperty("retryCount")
    @Schema(description = "Number of retry attempts", example = "0")
    @Builder.Default
    private Integer retryCount = 0;
    
    @Size(max = 500, message = "Error message cannot exceed {max} characters")
    @JsonProperty("errorMessage")
    @Schema(description = "Error message if notification failed")
    private String errorMessage;
    
    @Size(max = 100, message = "Template name cannot exceed {max} characters")
    @JsonProperty("templateName")
    @Schema(description = "Template used for this notification")
    private String templateName;
    
    @Size(max = 100, message = "Reference ID cannot exceed {max} characters")
    @JsonProperty("referenceId")
    @Schema(description = "Reference ID for related entity", example = "123")
    private String referenceId;
    
    @Size(max = 50, message = "Reference type cannot exceed {max} characters")
    @JsonProperty("referenceType")
    @Schema(description = "Type of referenced entity", example = "BOOK")
    private String referenceType;
    
    // ============= BUSINESS METHODS =============
    
    /**
     * Check if notification is pending
     */
    public boolean isPending() {
        return Notification.NotificationStatus.PENDING.equals(this.status);
    }
    
    /**
     * Check if notification is sent successfully
     */
    public boolean isSent() {
        return Notification.NotificationStatus.SENT.equals(this.status);
    }
    
    /**
     * Check if notification failed
     */
    public boolean isFailed() {
        return Notification.NotificationStatus.FAILED.equals(this.status);
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
        return String.format("NotificationDTO{id=%s, recipient='%s', type=%s, status=%s, active=%s}", 
                getId(), recipientEmail, notificationType, status, isActive());
    }
    
    @Override
    public String toDisplayString() {
        return String.format("Notification #%s: %s to %s (%s)",
                getId() != null ? getId() : "NEW",
                subject,
                getRecipientDisplay(),
                status != null ? status.getDisplayName() : "Unknown");
    }
}