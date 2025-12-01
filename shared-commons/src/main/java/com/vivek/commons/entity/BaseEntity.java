package com.vivek.commons.entity.base;

import com.vivek.commons.constants.AppConstants;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Enhanced base entity with comprehensive auditing features and proper Lombok integration
 * Provides common functionality for all domain entities using centralized constants
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Slf4j
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = AppConstants.Database.ID_COLUMN)
    @EqualsAndHashCode.Include
    private Long id;
    
    @CreatedDate
    @Column(name = AppConstants.Database.CREATED_AT_COLUMN, nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = AppConstants.Database.UPDATED_AT_COLUMN, nullable = false)
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(name = AppConstants.Database.CREATED_BY_COLUMN, updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    @Column(name = AppConstants.Database.UPDATED_BY_COLUMN)
    private String updatedBy;
    
    @Column(name = AppConstants.Database.IS_ACTIVE_COLUMN, nullable = false)
    @NotNull(message = "Active status cannot be null")
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
    
    @Version
    @Column(name = AppConstants.Database.VERSION_COLUMN)
    private Long version;
    
    // ============= LIFECYCLE METHODS =============
    
    /**
     * Marks entity as inactive (soft delete)
     */
    public void softDelete() {
        this.isActive = Boolean.FALSE;
        log.debug("Entity {} with id {} marked as inactive", getClass().getSimpleName(), id);
    }
    
    /**
     * Restores entity to active state
     */
    public void restore() {
        this.isActive = Boolean.TRUE;
        log.debug("Entity {} with id {} restored to active", getClass().getSimpleName(), id);
    }
    
    /**
     * Check if entity is active
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
    
    /**
     * Check if entity is inactive
     */
    public boolean isInactive() {
        return !isActive();
    }
    
    /**
     * Check if this is a new entity (not yet persisted)
     */
    public boolean isNew() {
        return this.id == null;
    }
    
    /**
     * Check if this is an existing entity (already persisted)
     */
    public boolean isPersisted() {
        return this.id != null;
    }
    
    // ============= UTILITY METHODS =============
    
    /**
     * Prepare entity for creation (reset system-managed fields)
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
    }
    
    /**
     * Get audit information as formatted string
     */
    public String getAuditInfo() {
        return String.format("Created: %s by %s, Updated: %s by %s, Version: %s", 
                createdAt, createdBy, updatedAt, updatedBy, version);
    }
    
    /**
     * Get a string representation suitable for logging
     */
    public String toLogString() {
        return String.format("%s{id=%s, active=%s, version=%s}", 
                getClass().getSimpleName(), id, isActive, version);
    }
    
    // ============= PRE-PERSIST CALLBACK =============
    
    @PrePersist
    protected void prePersist() {
        if (isActive == null) {
            isActive = Boolean.TRUE;
        }
        log.debug("Pre-persist callback for entity: {}", toLogString());
    }
    
    @PreUpdate
    protected void preUpdate() {
        log.debug("Pre-update callback for entity: {}", toLogString());
    }
    
    @PostPersist
    protected void postPersist() {
        log.info("Entity persisted: {}", toLogString());
    }
    
    @PostUpdate
    protected void postUpdate() {
        log.debug("Entity updated: {}", toLogString());
    }
    
    @PostRemove
    protected void postRemove() {
        log.info("Entity removed: {}", toLogString());
    }
}