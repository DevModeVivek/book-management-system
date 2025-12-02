package com.vivek.commons.service.base;

import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.entity.base.BaseEntity;
import com.vivek.commons.dto.base.BaseDTO;
import com.vivek.commons.exception.BookNotFoundException;
import com.vivek.commons.exception.ValidationException;
import com.vivek.commons.mapper.IMapper;
import com.vivek.commons.repository.base.BaseRepository;
import com.vivek.commons.common.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Enhanced base service with comprehensive functionality using centralized constants
 * Provides common CRUD operations and business logic for all service implementations
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public abstract class BaseService<E extends BaseEntity, D extends BaseDTO, ID> {
    
    protected final BaseRepository<E> repository;
    protected final IMapper<E, D> mapper;
    
    // ============= CREATE OPERATIONS =============
    
    @Transactional
    public D create(D dto) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".create",
                AppConstants.Logging.OPERATION_STARTED);
        
        try {
            validateForCreate(dto);
            dto.validateForCreation();
            
            E entity = mapper.toEntity(dto);
            E savedEntity = repository.save(entity);
            
            D result = mapper.toDTO(savedEntity);
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".create",
                    String.format("Created entity with ID: %s", savedEntity.getId()));
            
            return result;
            
        } catch (ValidationException | IllegalArgumentException e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".create",
                    "VALIDATION_ERROR", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".create",
                    "DATABASE_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.DATABASE_ERROR, e);
        }
    }
    
    // ============= READ OPERATIONS =============
    
    public Optional<D> getById(ID id) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".getById",
                String.format("Fetching entity with ID: %s", id));
        
        try {
            ValidationUtils.requireNonNull(id, "ID");
            
            return repository.findByIdAndIsActiveTrue((Long) id)
                    .map(entity -> {
                        D dto = mapper.toDTO(entity);
                        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                                AppConstants.Logging.CATEGORY_SERVICE,
                                getServiceName() + ".getById",
                                String.format("Found entity: %s", dto.toLogString()));
                        return dto;
                    });
                    
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".getById",
                    "FETCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.DATABASE_ERROR, e);
        }
    }
    
    public List<D> getAll() {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".getAll",
                AppConstants.Logging.OPERATION_STARTED);
        
        try {
            List<E> entities = repository.findAllByIsActiveTrue();
            List<D> dtos = mapper.toDTOList(entities);
            
            log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".getAll",
                    String.format("Retrieved %d entities", dtos.size()));
            
            return dtos;
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".getAll",
                    "FETCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.DATABASE_ERROR, e);
        }
    }
    
    public Page<D> getAll(Pageable pageable) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".getAll(pageable)",
                String.format("Fetching page: %d, size: %d", pageable.getPageNumber(), pageable.getPageSize()));
        
        try {
            ValidationUtils.validatePaginationParams(pageable.getPageNumber(), pageable.getPageSize());
            
            Page<D> result = repository.findAllByIsActiveTrue(pageable)
                    .map(mapper::toDTO);
            
            log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".getAll(pageable)",
                    String.format("Retrieved page %d with %d elements, total: %d", 
                            result.getNumber(), result.getNumberOfElements(), result.getTotalElements()));
            
            return result;
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".getAll(pageable)",
                    "FETCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.DATABASE_ERROR, e);
        }
    }
    
    // ============= UPDATE OPERATIONS =============
    
    @Transactional
    public D update(ID id, D dto) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".update",
                String.format("Updating entity with ID: %s", id));
        
        try {
            ValidationUtils.requireNonNull(id, "ID");
            validateForUpdate(id, dto);
            dto.validateForUpdate();
            
            E existingEntity = repository.findByIdAndIsActiveTrue((Long) id)
                    .orElseThrow(() -> BookNotFoundException.forId((Long) id));
            
            // Perform pre-update validation
            performPreUpdateValidation(existingEntity, dto);
            
            mapper.updateEntityFromDTO(dto, existingEntity);
            E savedEntity = repository.save(existingEntity);
            
            D result = mapper.toDTO(savedEntity);
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".update",
                    String.format("Updated entity with ID: %s", savedEntity.getId()));
            
            return result;
            
        } catch (BookNotFoundException | ValidationException | IllegalArgumentException e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".update",
                    "VALIDATION_ERROR", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".update",
                    "DATABASE_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.DATABASE_ERROR, e);
        }
    }
    
    // ============= DELETE OPERATIONS =============
    
    @Transactional
    public void delete(ID id) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".delete",
                String.format("Deleting entity with ID: %s", id));
        
        try {
            ValidationUtils.requireNonNull(id, "ID");
            
            if (!repository.existsByIdAndIsActiveTrue((Long) id)) {
                throw BookNotFoundException.forId((Long) id);
            }
            
            repository.deleteById((Long) id);
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".delete",
                    String.format("Deleted entity with ID: %s", id));
            
        } catch (BookNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".delete",
                    "DATABASE_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.DATABASE_ERROR, e);
        }
    }
    
    @Transactional
    public void softDelete(ID id) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".softDelete",
                String.format("Soft deleting entity with ID: %s", id));
        
        try {
            ValidationUtils.requireNonNull(id, "ID");
            
            if (!repository.existsByIdAndIsActiveTrue((Long) id)) {
                throw BookNotFoundException.forId((Long) id);
            }
            
            repository.softDeleteById((Long) id);
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".softDelete",
                    String.format("Soft deleted entity with ID: %s", id));
            
        } catch (BookNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".softDelete",
                    "DATABASE_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.DATABASE_ERROR, e);
        }
    }
    
    @Transactional
    public void restore(ID id) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".restore",
                String.format("Restoring entity with ID: %s", id));
        
        try {
            ValidationUtils.requireNonNull(id, "ID");
            
            repository.restoreById((Long) id);
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".restore",
                    String.format("Restored entity with ID: %s", id));
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".restore",
                    "DATABASE_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.DATABASE_ERROR, e);
        }
    }
    
    // ============= UTILITY OPERATIONS =============
    
    public boolean exists(ID id) {
        if (id == null) {
            return false;
        }
        
        try {
            return repository.existsByIdAndIsActiveTrue((Long) id);
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".exists",
                    "CHECK_ERROR", e.getMessage());
            return false;
        }
    }
    
    public boolean existsById(ID id) {
        return exists(id);
    }
    
    public long count() {
        try {
            return repository.countByIsActiveTrue();
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".count",
                    "COUNT_ERROR", e.getMessage());
            return 0;
        }
    }
    
    // ============= VALIDATION HOOKS =============
    
    /**
     * Override in child classes for custom create validation
     */
    protected void validateForCreate(D dto) {
        ValidationUtils.requireNonNull(dto, "DTO");
        if (dto.getId() != null) {
            throw new IllegalArgumentException("ID must be null for creation");
        }
    }
    
    /**
     * Override in child classes for custom update validation
     */
    protected void validateForUpdate(ID id, D dto) {
        ValidationUtils.requireNonNull(dto, "DTO");
        if (!id.equals(dto.getId())) {
            throw new IllegalArgumentException("Path ID must match DTO ID");
        }
    }
    
    /**
     * Override in child classes for custom pre-update business logic validation
     */
    protected void performPreUpdateValidation(E existingEntity, D dto) {
        // Default implementation - override in child classes for specific validation
    }
    
    /**
     * Get service name for logging purposes - override in child classes
     */
    protected abstract String getServiceName();
}