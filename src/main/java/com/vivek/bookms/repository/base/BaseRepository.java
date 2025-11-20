package com.vivek.bookms.repository.base;

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
 * Enhanced base repository with comprehensive functionality using Java field names
 * Provides common data access patterns for all domain repositories
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    
    // ============= CORE ACTIVE/INACTIVE OPERATIONS =============
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.isActive = true")
    Optional<T> findByIdAndIsActiveTrue(@Param("id") Long id);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = true ORDER BY e.createdAt DESC")
    List<T> findAllByIsActiveTrue();
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = true")
    List<T> findAllByIsActiveTrue(Sort sort);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = true")
    Page<T> findAllByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.isActive = true")
    long countByIsActiveTrue();
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.isActive = false")
    long countByIsActiveFalse();
    
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id AND e.isActive = true")
    boolean existsByIdAndIsActiveTrue(@Param("id") Long id);
    
    // ============= SOFT DELETE OPERATIONS WITH AUDIT TRACKING =============
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = false, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void softDeleteById(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = true, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void restoreById(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = false, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :ids")
    void softDeleteByIds(@Param("ids") List<Long> ids);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = true, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :ids")
    void restoreByIds(@Param("ids") List<Long> ids);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = false, e.updatedAt = CURRENT_TIMESTAMP, e.updatedBy = :updatedBy WHERE e.id = :id")
    void softDeleteByIdWithAudit(@Param("id") Long id, @Param("updatedBy") String updatedBy);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = true, e.updatedAt = CURRENT_TIMESTAMP, e.updatedBy = :updatedBy WHERE e.id = :id")
    void restoreByIdWithAudit(@Param("id") Long id, @Param("updatedBy") String updatedBy);
    
    // ============= ENHANCED FILTERING OPERATIONS =============
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt > :date AND e.isActive = true ORDER BY e.createdAt DESC")
    List<T> findActiveCreatedAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt > :date AND e.isActive = true")
    Page<T> findActiveCreatedAfter(@Param("date") LocalDateTime date, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt BETWEEN :startDate AND :endDate AND e.isActive = true ORDER BY e.createdAt DESC")
    List<T> findActiveCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt BETWEEN :startDate AND :endDate AND e.isActive = true")
    Page<T> findActiveCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdBy = :createdBy AND e.isActive = true ORDER BY e.createdAt DESC")
    List<T> findActiveByCreatedBy(@Param("createdBy") String createdBy);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdBy = :createdBy AND e.isActive = true")
    Page<T> findActiveByCreatedBy(@Param("createdBy") String createdBy, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedAt > :date AND e.isActive = true ORDER BY e.updatedAt DESC")
    List<T> findActiveUpdatedAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedAt > :date AND e.isActive = true")
    Page<T> findActiveUpdatedAfter(@Param("date") LocalDateTime date, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedBy = :updatedBy AND e.isActive = true ORDER BY e.updatedAt DESC")
    List<T> findActiveByUpdatedBy(@Param("updatedBy") String updatedBy);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedBy = :updatedBy AND e.isActive = true")
    Page<T> findActiveByUpdatedBy(@Param("updatedBy") String updatedBy, Pageable pageable);
    
    // ============= VERSION-BASED OPERATIONS =============
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.version = :version AND e.isActive = true")
    List<T> findActiveByVersion(@Param("version") Long version);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.version > :version AND e.isActive = true ORDER BY e.version ASC")
    List<T> findActiveWithVersionGreaterThan(@Param("version") Long version);
    
    // ============= STATISTICS AND ANALYTICS OPERATIONS =============
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.createdAt > :date AND e.isActive = true")
    long countActiveCreatedAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.createdAt BETWEEN :startDate AND :endDate AND e.isActive = true")
    long countActiveCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.createdBy = :createdBy AND e.isActive = true")
    long countActiveByCreatedBy(@Param("createdBy") String createdBy);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.updatedBy = :updatedBy AND e.isActive = true")
    long countActiveByUpdatedBy(@Param("updatedBy") String updatedBy);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.isActive = false")
    long countInactive();
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e")
    long countTotal();
    
    // ============= ADVANCED QUERY OPERATIONS =============
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = :isActive ORDER BY e.createdAt DESC")
    List<T> findByActiveStatus(@Param("isActive") Boolean isActive);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = :isActive")
    Page<T> findByActiveStatus(@Param("isActive") Boolean isActive, Pageable pageable);
    
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.updatedAt DESC")
    List<T> findAllOrderByLastModified();
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = true ORDER BY e.updatedAt DESC")
    List<T> findActiveOrderByLastModified();
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = true ORDER BY e.createdAt ASC")
    List<T> findActiveOrderByCreatedAsc();
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = true ORDER BY e.createdAt DESC")
    List<T> findActiveOrderByCreatedDesc();
    
    // ============= BATCH OPERATIONS =============
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = :isActive, e.updatedAt = CURRENT_TIMESTAMP WHERE e.createdBy = :createdBy")
    int updateActiveStatusByCreatedBy(@Param("createdBy") String createdBy, @Param("isActive") Boolean isActive);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = :isActive, e.updatedAt = CURRENT_TIMESTAMP WHERE e.createdAt < :date")
    int updateActiveStatusByCreatedBefore(@Param("date") LocalDateTime date, @Param("isActive") Boolean isActive);
    
    // ============= EXISTENCE CHECK OPERATIONS =============
    
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.createdBy = :createdBy AND e.isActive = true")
    boolean existsActiveByCreatedBy(@Param("createdBy") String createdBy);
    
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.updatedBy = :updatedBy AND e.isActive = true")
    boolean existsActiveByUpdatedBy(@Param("updatedBy") String updatedBy);
    
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.createdAt > :date AND e.isActive = true")
    boolean existsActiveCreatedAfter(@Param("date") LocalDateTime date);
}