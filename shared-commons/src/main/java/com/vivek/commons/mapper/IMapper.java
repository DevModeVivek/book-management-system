package com.vivek.commons.mapper;

import java.util.List;

/**
 * Enhanced generic mapper interface for entity-DTO conversions with list support
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
     * Convert list of entities to list of DTOs
     * @param entities List of entities to convert
     * @return List of converted DTOs
     */
    List<D> toDTOList(List<E> entities);
    
    /**
     * Convert list of DTOs to list of entities
     * @param dtos List of DTOs to convert
     * @return List of converted entities
     */
    List<E> toEntityList(List<D> dtos);
    
    /**
     * Update entity with DTO data
     * @param dto Source DTO
     * @param entity Target entity
     */
    void updateEntityFromDTO(D dto, E entity);
}