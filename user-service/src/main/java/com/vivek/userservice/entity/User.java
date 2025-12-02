package com.vivek.userservice.entity;

import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.entity.base.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * User entity extending BaseEntity with comprehensive validation
 * Follows proper inheritance pattern with Lombok SuperBuilder integration
 */
@Entity
@Table(name = AppConstants.Database.USERS_TABLE,
       indexes = {
           @Index(name = "idx_user_email", columnList = "email", unique = true),
           @Index(name = "idx_user_username", columnList = "username", unique = true),
           @Index(name = "idx_user_active", columnList = AppConstants.Database.IS_ACTIVE_COLUMN)
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true, exclude = {"password"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class User extends BaseEntity {
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between {min} and {max} characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores")
    @EqualsAndHashCode.Include
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed {max} characters")
    @EqualsAndHashCode.Include
    private String email;
    
    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between {min} and {max} characters")
    private String password;
    
    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between {min} and {max} characters")
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "First name contains invalid characters")
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between {min} and {max} characters")
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "Last name contains invalid characters")
    private String lastName;
    
    @Column(name = "date_of_birth")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @Column(name = "phone_number", length = 20)
    @Size(max = 20, message = "Phone number cannot exceed {max} characters")
    @Pattern(regexp = "^[+]?[0-9\\s()-]+$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @NotNull(message = "Role is required")
    @Builder.Default
    private UserRole role = UserRole.USER;
    
    @Column(name = "account_locked", nullable = false)
    @Builder.Default
    private Boolean accountLocked = Boolean.FALSE;
    
    @Column(name = "account_enabled", nullable = false)
    @Builder.Default
    private Boolean accountEnabled = Boolean.TRUE;
    
    // ============= BUSINESS METHODS =============
    
    /**
     * Get full name
     */
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
    
    /**
     * Check if account is locked
     */
    public boolean isAccountLocked() {
        return Boolean.TRUE.equals(this.accountLocked);
    }
    
    /**
     * Check if account is enabled
     */
    public boolean isAccountEnabled() {
        return Boolean.TRUE.equals(this.accountEnabled);
    }
    
    /**
     * Lock the user account
     */
    public void lockAccount() {
        this.accountLocked = Boolean.TRUE;
    }
    
    /**
     * Unlock the user account
     */
    public void unlockAccount() {
        this.accountLocked = Boolean.FALSE;
    }
    
    /**
     * Enable the user account
     */
    public void enableAccount() {
        this.accountEnabled = Boolean.TRUE;
    }
    
    /**
     * Disable the user account
     */
    public void disableAccount() {
        this.accountEnabled = Boolean.FALSE;
    }
    
    /**
     * Check if user has admin role
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }
    
    /**
     * Check if user has moderator role
     */
    public boolean isModerator() {
        return UserRole.MODERATOR.equals(this.role);
    }
    
    /**
     * Get age from date of birth
     */
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
    
    @Override
    public String toLogString() {
        return String.format("User{id=%s, username='%s', email='%s', role=%s, active=%s}", 
                getId(), username, email, role, isActive());
    }
    
    // ============= VALIDATION HELPER METHODS =============
    
    /**
     * Validate business rules for the user
     */
    public void validateBusinessRules() {
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalStateException("Date of birth cannot be in the future");
        }
        
        if (role == null) {
            throw new IllegalStateException("Role cannot be null");
        }
    }
    
    @PrePersist
    @PreUpdate
    protected void validateEntity() {
        validateBusinessRules();
        super.prePersist();
    }
    
    // ============= USER ROLE ENUM =============
    
    public enum UserRole {
        USER("User"),
        MODERATOR("Moderator"), 
        ADMIN("Administrator");
        
        private final String displayName;
        
        UserRole(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}