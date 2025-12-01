package com.vivek.commons.dto.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vivek.commons.constants.AppConstants;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Enhanced Base DTO class with comprehensive functionality using centralized constants
 * Provides common fields and behavior for all DTOs with proper Lombok integration
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Slf4j
public abstract class BaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Active status cannot be null")
    @JsonProperty("isActive")
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private String updatedBy;

    @JsonIgnore
    private Long version;

    // ============= LIFECYCLE METHODS =============

    /**
     * Check if DTO represents an active entity
     */
    @JsonIgnore
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    /**
     * Check if DTO represents an inactive entity
     */
    @JsonIgnore
    public boolean isInactive() {
        return !isActive();
    }

    /**
     * Check if this is a new entity (no ID assigned yet)
     */
    @JsonIgnore
    public boolean isNew() {
        return this.id == null;
    }

    /**
     * Check if this is an existing entity
     */
    @JsonIgnore
    public boolean isExisting() {
        return this.id != null;
    }

    /**
     * Mark entity as active
     */
    public void activate() {
        this.isActive = Boolean.TRUE;
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_MAPPER,
                getClass().getSimpleName(),
                "Entity marked as active");
    }

    /**
     * Mark entity as inactive (soft delete)
     */
    public void deactivate() {
        this.isActive = Boolean.FALSE;
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_MAPPER,
                getClass().getSimpleName(),
                "Entity marked as inactive");
    }

    /**
     * Prepare DTO for creation (reset system fields)
     */
    public void prepareForCreation() {
        this.id = null;
        this.createdAt = null;
        this.updatedAt = null;
        this.createdBy = null;
        this.updatedBy = null;
        this.version = null;
        if (this.isActive == null) {
            this.isActive = Boolean.TRUE;
        }
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_MAPPER,
                getClass().getSimpleName(),
                "Prepared for creation");
    }

    // ============= UTILITY METHODS =============

    /**
     * Get audit information as formatted string
     */
    @JsonIgnore
    public String getAuditInfo() {
        return String.format("Created: %s, Updated: %s, Version: %s",
                createdAt, updatedAt, version);
    }

    /**
     * Get a string representation of the DTO for logging
     */
    @JsonIgnore
    public String toLogString() {
        return String.format("%s{id=%s, active=%s, version=%s}",
                getClass().getSimpleName(), id, isActive, version);
    }

    /**
     * Get a brief summary suitable for display
     */
    @JsonIgnore
    public String toDisplayString() {
        return String.format("%s #%s %s",
                getClass().getSimpleName().replace("DTO", ""),
                id != null ? id : "NEW",
                isActive() ? "(Active)" : "(Inactive)");
    }

    // ============= VALIDATION HELPER METHODS =============

    /**
     * Validate that this DTO has required fields for creation
     */
    public void validateForCreation() {
        // Override in child classes for specific validation
        if (isActive == null) {
            throw new IllegalStateException("Active status must be set for creation");
        }
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                getClass().getSimpleName(),
                "Validation passed for creation");
    }

    /**
     * Validate that this DTO has required fields for update
     */
    public void validateForUpdate() {
        if (id == null) {
            throw new IllegalStateException("ID must be set for update operations");
        }
        if (isActive == null) {
            throw new IllegalStateException("Active status must be set for update");
        }
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                getClass().getSimpleName(),
                "Validation passed for update");
    }

    /**
     * Check if this DTO represents the same entity as another
     */
    public boolean isSameEntity(BaseDTO other) {
        if (other == null) {
            return false;
        }
        return this.id != null && this.id.equals(other.getId());
    }
}