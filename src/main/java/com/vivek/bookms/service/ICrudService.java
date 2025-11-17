package com.vivek.bookms.service;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD service interface for basic operations
 * @param <T> Entity/DTO type
 * @param <ID> ID type
 */
public interface ICrudService<T, ID> {
    
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
    
    /**
     * Check if entity exists by ID
     * @param id Entity ID
     * @return true if exists, false otherwise
     */
    boolean exists(ID id);
    
    /**
     * Count total entities
     * @return Total count
     */
    long count();
}