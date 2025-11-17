package com.vivek.bookms.mapper;

/**
 * Generic mapper interface for entity-DTO conversions
 * @param <E> Entity type
 * @param <D> DTO type
 */
public interface IMapper<E, D> {
    
    /**
     * Convert entity to DTO
     * @param entity Entity to convert
     * @return Converted DTO
     */
    D toDTO(E entity);
    
    /**
     * Convert DTO to entity
     * @param dto DTO to convert
     * @return Converted entity
     */
    E toEntity(D dto);
    
    /**
     * Update entity with DTO data
     * @param dto Source DTO
     * @param entity Target entity
     */
    void updateEntityFromDTO(D dto, E entity);
}