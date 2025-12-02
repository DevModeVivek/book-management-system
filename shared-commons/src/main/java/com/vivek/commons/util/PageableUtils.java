package com.vivek.commons.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating reusable pagination and sorting configurations
 * Provides fluent API for building Pageable objects with dynamic sorting
 */
public class PageableUtils {
    
    private int page = 0;
    private int size = 10;
    private final List<Sort.Order> orders = new ArrayList<>();
    
    /**
     * Create new PageableUtils instance
     */
    public static PageableUtils of() {
        return new PageableUtils();
    }
    
    /**
     * Set page number (0-based)
     */
    public PageableUtils page(int page) {
        this.page = Math.max(0, page);
        return this;
    }
    
    /**
     * Set page size
     */
    public PageableUtils size(int size) {
        this.size = Math.max(1, size);
        return this;
    }
    
    /**
     * Add ascending sort by field
     */
    public PageableUtils sortBy(String field) {
        orders.add(Sort.Order.asc(field));
        return this;
    }
    
    /**
     * Add descending sort by field
     */
    public PageableUtils sortByDesc(String field) {
        orders.add(Sort.Order.desc(field));
        return this;
    }
    
    /**
     * Add sort order
     */
    public PageableUtils sort(Sort.Direction direction, String field) {
        orders.add(new Sort.Order(direction, field));
        return this;
    }
    
    /**
     * Clear all sort orders
     */
    public PageableUtils clearSort() {
        orders.clear();
        return this;
    }
    
    /**
     * Build Pageable object
     */
    public Pageable build() {
        if (orders.isEmpty()) {
            return PageRequest.of(page, size);
        }
        return PageRequest.of(page, size, Sort.by(orders));
    }
    
    /**
     * Common pagination presets
     */
    public static class Presets {
        
        public static Pageable defaultPage() {
            return PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        }
        
        public static Pageable smallPage() {
            return PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        }
        
        public static Pageable largePage() {
            return PageRequest.of(0, 50, Sort.by(Sort.Direction.ASC, "id"));
        }
        
        public static Pageable sortByCreatedDate() {
            return PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        
        public static Pageable sortByUpdatedDate() {
            return PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        }
    }
}