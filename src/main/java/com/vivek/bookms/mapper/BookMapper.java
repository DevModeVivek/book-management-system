package com.vivek.bookms.mapper;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Book mapper implementation using ModelMapper for dependency injection
 * Provides enhanced mapping capabilities with proper DI support
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookMapper implements IBookMapper {
    
    private final ModelMapper modelMapper;
    
    @Override
    public BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }
        
        try {
            return modelMapper.map(book, BookDTO.class);
        } catch (Exception e) {
            log.error("Error mapping Book entity to DTO: {}", e.getMessage());
            throw new RuntimeException("Mapping failed", e);
        }
    }
    
    @Override
    public Book toEntity(BookDTO dto) {
        if (dto == null) {
            return null;
        }
        
        try {
            return modelMapper.map(dto, Book.class);
        } catch (Exception e) {
            log.error("Error mapping BookDTO to entity: {}", e.getMessage());
            throw new RuntimeException("Mapping failed", e);
        }
    }
    
    @Override
    public List<BookDTO> toDTOList(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return List.of();
        }
        
        return books.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> toEntityList(List<BookDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public void updateEntityFromDTO(BookDTO dto, Book book) {
        if (dto == null || book == null) {
            return;
        }
        
        try {
            // Skip ID and audit fields during update
            modelMapper.getConfiguration().setPropertyCondition(context -> 
                !context.getMapping().getLastDestinationProperty().getName().equals("id") &&
                !context.getMapping().getLastDestinationProperty().getName().equals("createdAt") &&
                !context.getMapping().getLastDestinationProperty().getName().equals("createdBy"));
            
            modelMapper.map(dto, book);
        } catch (Exception e) {
            log.error("Error updating Book entity from DTO: {}", e.getMessage());
            throw new RuntimeException("Update mapping failed", e);
        }
    }
}