package com.vivek.bookms.repository.base;

import com.vivek.bookms.constants.AppConstants;
import com.vivek.bookms.entity.base.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enhanced base repository with comprehensive functionality using centralized constants
 * Provides common data access patterns for all domain repositories
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    
    // ============= CORE ACTIVE/INACTIVE OPERATIONS =============
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Optional<T> findByIdAndIsActiveTrue(@Param("id") Long id);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY e." + AppConstants.Database.CREATED_AT_COLUMN + " DESC")
    List<T> findAllByIsActiveTrue();
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    List<T> findAllByIsActiveTrue(Sort sort);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Page<T> findAllByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countByIsActiveTrue();
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = false")
    long countByIsActiveFalse();
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.id = :id AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    boolean existsByIdAndIsActiveTrue(@Param("id") Long id);
    
    // ============= SOFT DELETE OPERATIONS WITH AUDIT TRACKING =============
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = false, " +
           "e." + AppConstants.Database.UPDATED_AT_COLUMN + " = CURRENT_TIMESTAMP " +
           "WHERE e.id = :id")
    void softDeleteById(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true, " +
           "e." + AppConstants.Database.UPDATED_AT_COLUMN + " = CURRENT_TIMESTAMP " +
           "WHERE e.id = :id")
    void restoreById(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = false, " +
           "e." + AppConstants.Database.UPDATED_AT_COLUMN + " = CURRENT_TIMESTAMP " +
           "WHERE e.id IN :ids")
    void softDeleteByIds(@Param("ids") List<Long> ids);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true, " +
           "e." + AppConstants.Database.UPDATED_AT_COLUMN + " = CURRENT_TIMESTAMP " +
           "WHERE e.id IN :ids")
    void restoreByIds(@Param("ids") List<Long> ids);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = false, " +
           "e." + AppConstants.Database.UPDATED_AT_COLUMN + " = CURRENT_TIMESTAMP, " +
           "e." + AppConstants.Database.UPDATED_BY_COLUMN + " = :updatedBy " +
           "WHERE e.id = :id")
    void softDeleteByIdWithAudit(@Param("id") Long id, @Param("updatedBy") String updatedBy);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true, " +
           "e." + AppConstants.Database.UPDATED_AT_COLUMN + " = CURRENT_TIMESTAMP, " +
           "e." + AppConstants.Database.UPDATED_BY_COLUMN + " = :updatedBy " +
           "WHERE e.id = :id")
    void restoreByIdWithAudit(@Param("id") Long id, @Param("updatedBy") String updatedBy);
    
    // ============= ENHANCED FILTERING OPERATIONS =============
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.CREATED_AT_COLUMN + " > :date " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY e." + AppConstants.Database.CREATED_AT_COLUMN + " DESC")
    List<T> findActiveCreatedAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.CREATED_AT_COLUMN + " > :date " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Page<T> findActiveCreatedAfter(@Param("date") LocalDateTime date, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.CREATED_AT_COLUMN + " BETWEEN :startDate AND :endDate " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY e." + AppConstants.Database.CREATED_AT_COLUMN + " DESC")
    List<T> findActiveCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.CREATED_AT_COLUMN + " BETWEEN :startDate AND :endDate " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Page<T> findActiveCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.CREATED_BY_COLUMN + " = :createdBy " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY e." + AppConstants.Database.CREATED_AT_COLUMN + " DESC")
    List<T> findActiveByCreatedBy(@Param("createdBy") String createdBy);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.CREATED_BY_COLUMN + " = :createdBy " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Page<T> findActiveByCreatedBy(@Param("createdBy") String createdBy, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.UPDATED_AT_COLUMN + " > :date " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY e." + AppConstants.Database.UPDATED_AT_COLUMN + " DESC")
    List<T> findActiveUpdatedAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.UPDATED_AT_COLUMN + " > :date " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Page<T> findActiveUpdatedAfter(@Param("date") LocalDateTime date, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.UPDATED_BY_COLUMN + " = :updatedBy " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY e." + AppConstants.Database.UPDATED_AT_COLUMN + " DESC")
    List<T> findActiveByUpdatedBy(@Param("updatedBy") String updatedBy);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.UPDATED_BY_COLUMN + " = :updatedBy " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Page<T> findActiveByUpdatedBy(@Param("updatedBy") String updatedBy, Pageable pageable);
    
    // ============= VERSION-BASED OPERATIONS =============
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.VERSION_COLUMN + " = :version " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    List<T> findActiveByVersion(@Param("version") Long version);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.VERSION_COLUMN + " > :version " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY e." + AppConstants.Database.VERSION_COLUMN + " ASC")
    List<T> findActiveWithVersionGreaterThan(@Param("version") Long version);
    
    // ============= STATISTICS AND ANALYTICS OPERATIONS =============
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e." + AppConstants.Database.CREATED_AT_COLUMN + " > :date " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countActiveCreatedAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e." + AppConstants.Database.CREATED_AT_COLUMN + " BETWEEN :startDate AND :endDate " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countActiveCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e." + AppConstants.Database.CREATED_BY_COLUMN + " = :createdBy " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countActiveByCreatedBy(@Param("createdBy") String createdBy);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e." + AppConstants.Database.UPDATED_BY_COLUMN + " = :updatedBy " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countActiveByUpdatedBy(@Param("updatedBy") String updatedBy);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = false")
    long countInactive();
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e")
    long countTotal();
    
    // ============= ADVANCED QUERY OPERATIONS =============
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = :isActive " +
           "ORDER BY e." + AppConstants.Database.CREATED_AT_COLUMN + " DESC")
    List<T> findByActiveStatus(@Param("isActive") Boolean isActive);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = :isActive")
    Page<T> findByActiveStatus(@Param("isActive") Boolean isActive, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e ORDER BY e." + AppConstants.Database.UPDATED_AT_COLUMN + " DESC")
    List<T> findAllOrderByLastModified();
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY e." + AppConstants.Database.UPDATED_AT_COLUMN + " DESC")
    List<T> findActiveOrderByLastModified();
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY e." + AppConstants.Database.CREATED_AT_COLUMN + " ASC")
    List<T> findActiveOrderByCreatedAsc();
    
    @Query("SELECT e FROM #{#entityName} e WHERE e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY e." + AppConstants.Database.CREATED_AT_COLUMN + " DESC")
    List<T> findActiveOrderByCreatedDesc();
    
    // ============= BATCH OPERATIONS =============
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = :isActive, " +
           "e." + AppConstants.Database.UPDATED_AT_COLUMN + " = CURRENT_TIMESTAMP " +
           "WHERE e." + AppConstants.Database.CREATED_BY_COLUMN + " = :createdBy")
    int updateActiveStatusByCreatedBy(@Param("createdBy") String createdBy, @Param("isActive") Boolean isActive);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = :isActive, " +
           "e." + AppConstants.Database.UPDATED_AT_COLUMN + " = CURRENT_TIMESTAMP " +
           "WHERE e." + AppConstants.Database.CREATED_AT_COLUMN + " < :date")
    int updateActiveStatusByCreatedBefore(@Param("date") LocalDateTime date, @Param("isActive") Boolean isActive);
    
    // ============= EXISTENCE CHECK OPERATIONS =============
    
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e " +
           "WHERE e." + AppConstants.Database.CREATED_BY_COLUMN + " = :createdBy " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    boolean existsActiveByCreatedBy(@Param("createdBy") String createdBy);
    
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e " +
           "WHERE e." + AppConstants.Database.UPDATED_BY_COLUMN + " = :updatedBy " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    boolean existsActiveByUpdatedBy(@Param("updatedBy") String updatedBy);
    
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e " +
           "WHERE e." + AppConstants.Database.CREATED_AT_COLUMN + " > :date " +
           "AND e." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    boolean existsActiveCreatedAfter(@Param("date") LocalDateTime date);
}