package com.vivek.notificationservice.controller;

import com.vivek.notificationservice.dto.NotificationDTO;
import com.vivek.notificationservice.entity.Notification;
import com.vivek.notificationservice.service.INotificationService;
import com.vivek.commons.constants.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Notification Controller for Asynchronous Messaging
 * Provides REST endpoints for notification management and monitoring
 */
@RestController
@RequestMapping("/notifications")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Notification Management", description = "APIs for managing notifications and monitoring event-driven messaging")
public class NotificationController {
    
    private final INotificationService notificationService;
    
    @Operation(summary = "Get all notifications", description = "Retrieve all notifications with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllNotifications(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_CONTROLLER,
                "NotificationController.getAllNotifications",
                String.format("Fetching notifications - page: %d, size: %d", 
                        pageable.getPageNumber(), pageable.getPageSize()));
        
        try {
            Page<NotificationDTO> notifications = notificationService.getAllNotifications(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notifications retrieved successfully");
            response.put("content", notifications.getContent());
            response.put("page", Map.of(
                    "number", notifications.getNumber(),
                    "size", notifications.getSize(),
                    "totalElements", notifications.getTotalElements(),
                    "totalPages", notifications.getTotalPages(),
                    "first", notifications.isFirst(),
                    "last", notifications.isLast()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_CONTROLLER,
                    "NotificationController.getAllNotifications",
                    "FETCH_ERROR", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to retrieve notifications");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @Operation(summary = "Get notification by ID", description = "Retrieve a specific notification by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notification"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getNotificationById(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_CONTROLLER,
                "NotificationController.getNotificationById",
                String.format("Fetching notification with ID: %d", id));
        
        try {
            Optional<NotificationDTO> notification = notificationService.getNotificationById(id);
            
            if (notification.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Notification retrieved successfully");
                response.put("data", notification.get());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Notification not found");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_CONTROLLER,
                    "NotificationController.getNotificationById",
                    "FETCH_ERROR", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to retrieve notification");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @Operation(summary = "Get notifications for recipient", description = "Retrieve all notifications for a specific recipient email")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications")
    @GetMapping("/recipient/{email}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getNotificationsForRecipient(
            @Parameter(description = "Recipient email") @PathVariable String email) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_CONTROLLER,
                "NotificationController.getNotificationsForRecipient",
                String.format("Fetching notifications for recipient: %s", email));
        
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsForRecipient(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", String.format("Found %d notifications for %s", notifications.size(), email));
            response.put("data", notifications);
            response.put("count", notifications.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_CONTROLLER,
                    "NotificationController.getNotificationsForRecipient",
                    "FETCH_ERROR", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to retrieve notifications for recipient");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @Operation(summary = "Get notifications by status", description = "Retrieve notifications filtered by status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getNotificationsByStatus(
            @Parameter(description = "Notification status") @PathVariable Notification.NotificationStatus status) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_CONTROLLER,
                "NotificationController.getNotificationsByStatus",
                String.format("Fetching notifications with status: %s", status));
        
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsByStatus(status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", String.format("Found %d notifications with status %s", notifications.size(), status));
            response.put("data", notifications);
            response.put("count", notifications.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_CONTROLLER,
                    "NotificationController.getNotificationsByStatus",
                    "FETCH_ERROR", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to retrieve notifications by status");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @Operation(summary = "Send manual notification", description = "Send a notification manually (for testing purposes)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid notification data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> sendNotification(
            @Valid @RequestBody NotificationDTO notificationDTO) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_CONTROLLER,
                "NotificationController.sendNotification",
                String.format("Sending manual notification to: %s", notificationDTO.getRecipientEmail()));
        
        try {
            NotificationDTO sentNotification = notificationService.sendNotification(notificationDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notification sent successfully");
            response.put("data", sentNotification);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_CONTROLLER,
                    "NotificationController.sendNotification",
                    "SEND_ERROR", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to send notification: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @Operation(summary = "Retry failed notifications", description = "Retry all failed notifications that can be retried")
    @ApiResponse(responseCode = "200", description = "Retry operation completed")
    @PostMapping("/retry-failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> retryFailedNotifications() {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_CONTROLLER,
                "NotificationController.retryFailedNotifications",
                "Starting retry of failed notifications");
        
        try {
            int retriedCount = notificationService.retryFailedNotifications();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", String.format("Retried %d failed notifications", retriedCount));
            response.put("retriedCount", retriedCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_CONTROLLER,
                    "NotificationController.retryFailedNotifications",
                    "RETRY_ERROR", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to retry notifications: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    @ApiResponse(responseCode = "200", description = "Notification marked as read")
    @PutMapping("/{id}/mark-read")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_CONTROLLER,
                "NotificationController.markAsRead",
                String.format("Marking notification as read: %d", id));
        
        try {
            notificationService.markAsRead(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notification marked as read");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_CONTROLLER,
                    "NotificationController.markAsRead",
                    "UPDATE_ERROR", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to mark notification as read");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @Operation(summary = "Delete notification", description = "Soft delete a notification")
    @ApiResponse(responseCode = "204", description = "Notification deleted successfully")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_CONTROLLER,
                "NotificationController.deleteNotification",
                String.format("Deleting notification: %d", id));
        
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_CONTROLLER,
                    "NotificationController.deleteNotification",
                    "DELETE_ERROR", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}