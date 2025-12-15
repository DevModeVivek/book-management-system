package com.vivek.bookservice.controller;

import com.vivek.bookservice.service.IBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cache Management Controller for Phase 2: Distributed Caching & Performance Optimization
 * Provides endpoints for cache monitoring, control, and performance analysis
 */
@RestController
@RequestMapping("/cache")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Cache Management", description = "APIs for Redis cache monitoring and control")
public class CacheController {
    
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final IBookService bookService;
    
    @GetMapping("/info")
    @Operation(summary = "Get cache information", description = "Retrieve information about all configured caches")
    public ResponseEntity<Map<String, Object>> getCacheInfo() {
        log.info("Retrieving cache information");
        
        Map<String, Object> cacheInfo = new HashMap<>();
        
        // Get cache names
        cacheInfo.put("cacheNames", cacheManager.getCacheNames());
        
        // Get Redis info
        try {
            Set<String> keys = redisTemplate.keys("*");
            cacheInfo.put("totalKeys", keys != null ? keys.size() : 0);
            cacheInfo.put("redisConnection", "Connected");
        } catch (Exception e) {
            cacheInfo.put("redisConnection", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(cacheInfo);
    }
    
    @GetMapping("/keys")
    @Operation(summary = "Get all cache keys", description = "Retrieve all Redis cache keys")
    public ResponseEntity<Map<String, Object>> getAllKeys() {
        log.info("Retrieving all cache keys");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Set<String> keys = redisTemplate.keys("*");
            response.put("keys", keys);
            response.put("count", keys != null ? keys.size() : 0);
            response.put("status", "success");
        } catch (Exception e) {
            log.error("Error retrieving cache keys: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/clear")
    @Operation(summary = "Clear all caches", description = "Clear all Redis caches")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        log.info("Clearing all caches");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Clear application caches
            bookService.clearAllBookCaches();
            
            for (String cacheName : cacheManager.getCacheNames()) {
                if (cacheManager.getCache(cacheName) != null) {
                    cacheManager.getCache(cacheName).clear();
                }
            }
            
            // Clear all Redis keys
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            
            response.put("status", "success");
            response.put("message", "All caches cleared successfully");
            response.put("clearedCaches", cacheManager.getCacheNames());
            
        } catch (Exception e) {
            log.error("Error clearing caches: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/clear/{cacheName}")
    @Operation(summary = "Clear specific cache", description = "Clear a specific Redis cache by name")
    public ResponseEntity<Map<String, Object>> clearSpecificCache(@PathVariable String cacheName) {
        log.info("Clearing cache: {}", cacheName);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (cacheManager.getCache(cacheName) != null) {
                cacheManager.getCache(cacheName).clear();
                response.put("status", "success");
                response.put("message", "Cache '" + cacheName + "' cleared successfully");
            } else {
                response.put("status", "error");
                response.put("message", "Cache '" + cacheName + "' not found");
            }
        } catch (Exception e) {
            log.error("Error clearing cache '{}': {}", cacheName, e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/test/performance")
    @Operation(summary = "Test cache performance", description = "Run performance tests with and without cache")
    public ResponseEntity<Map<String, Object>> testCachePerformance(@RequestParam(defaultValue = "10") int iterations) {
        log.info("Testing cache performance with {} iterations", iterations);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Clear cache first
            bookService.clearAllBookCaches();
            
            // Test without cache (first run)
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                bookService.getAll();
            }
            long withoutCacheTime = System.currentTimeMillis() - startTime;
            
            // Test with cache (subsequent runs)
            startTime = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                bookService.getAll();
            }
            long withCacheTime = System.currentTimeMillis() - startTime;
            
            // Calculate performance improvement
            double improvementPercent = ((double)(withoutCacheTime - withCacheTime) / withoutCacheTime) * 100;
            
            response.put("status", "success");
            response.put("iterations", iterations);
            response.put("withoutCache", withoutCacheTime + "ms");
            response.put("withCache", withCacheTime + "ms");
            response.put("improvement", String.format("%.2f%%", improvementPercent));
            response.put("speedupFactor", String.format("%.2fx", (double)withoutCacheTime / withCacheTime));
            
        } catch (Exception e) {
            log.error("Error during cache performance test: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get cache statistics", description = "Get detailed cache statistics and metrics")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        log.info("Retrieving cache statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Basic cache info
            stats.put("cacheNames", cacheManager.getCacheNames());
            
            // Redis key count by cache type
            Map<String, Integer> keyCountsByCache = new HashMap<>();
            for (String cacheName : cacheManager.getCacheNames()) {
                Set<String> keys = redisTemplate.keys(cacheName + "*");
                keyCountsByCache.put(cacheName, keys != null ? keys.size() : 0);
            }
            stats.put("keyCountsByCache", keyCountsByCache);
            
            // Total keys
            Set<String> allKeys = redisTemplate.keys("*");
            stats.put("totalKeys", allKeys != null ? allKeys.size() : 0);
            
            stats.put("status", "success");
            stats.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error retrieving cache statistics: {}", e.getMessage());
            stats.put("status", "error");
            stats.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(stats);
    }
}