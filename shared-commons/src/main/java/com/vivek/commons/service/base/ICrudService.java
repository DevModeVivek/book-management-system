package com.vivek.commons.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Enhanced CRUD service interface with comprehensive operations including pagination
 * @param <T> Entity/DTO type
 * @param <ID> ID type
 */
public interface ICrudService<T, ID> {
    
    // ============= BASIC CRUD OPERATIONS =============
    
    /**
     * Create a new entity
     * @param entity Entity to create
     * @return Created entity
     */
    T create(T entity);
    
    /**
     * Get entity by ID
     * @param id Entity ID
     * @return Entity if found
     */
    Optional<T> getById(ID id);
    
    /**
     * Get all entities
     * @return List of all entities
     */
    List<T> getAll();
    
    /**
     * Get all entities with pagination
     * @param pageable Pagination information
     * @return Page of entities
     */
    Page<T> getAll(Pageable pageable);
    
    /**
     * Update existing entity
     * @param id Entity ID
     * @param entity Updated entity data
     * @return Updated entity
     */
    T update(ID id, T entity);
    
    /**
     * Delete entity by ID
     * @param id Entity ID
     */
    void delete(ID id);
    
    // ============= SOFT DELETE OPERATIONS =============
    
    /**
     * Soft delete entity by ID (mark as inactive)
     * @param id Entity ID
     */
    void softDelete(ID id);
    
    /**
     * Restore a soft-deleted entity by ID
     * @param id Entity ID
     */
    void restore(ID id);
    
    // ============= EXISTENCE AND COUNT OPERATIONS =============
    
    /**
     * Check if entity exists by ID
     * @param id Entity ID
     * @return true if exists, false otherwise
     */
    boolean exists(ID id);
    
    /**
     * Check if entity exists by ID (alternative method name)
     * @param id Entity ID
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);
    
    /**
     * Count total entities
     * @return Total count
     */
    long count();
}