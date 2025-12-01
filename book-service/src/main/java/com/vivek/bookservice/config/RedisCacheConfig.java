package com.vivek.bookservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Cache Configuration for Phase 2: Distributed Caching & Performance Optimization
 * Configures TTL and eviction policies for different cache regions
 * FIXED: Removed duplicate ObjectMapper bean to avoid conflicts
 */
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, 
                                   @Qualifier("objectMapper") ObjectMapper objectMapper) {
        // Use the existing ObjectMapper from BookServiceConfig
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Default 10 minutes TTL
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();
        
        // Specific cache configurations with different TTL
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Books cache - 10 minutes TTL
        cacheConfigurations.put("books", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // Book search cache - 5 minutes TTL (searches change frequently)
        cacheConfigurations.put("book-search", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // External books cache - 30 minutes TTL (external data is more stable)
        cacheConfigurations.put("external-books", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // Book by ID cache - 15 minutes TTL
        cacheConfigurations.put("book-by-id", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        
        // Book by ISBN cache - 20 minutes TTL (ISBN is unique and stable)
        cacheConfigurations.put("book-by-isbn", defaultConfig.entryTtl(Duration.ofMinutes(20)));
        
        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       @Qualifier("objectMapper") ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use the existing ObjectMapper with JSR310 support
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // Configure serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public CacheManagerCustomizer<RedisCacheManager> cacheManagerCustomizer() {
        return cacheManager -> {
            // Additional customization if needed
            System.out.println("Redis Cache Manager initialized with custom configurations");
        };
    }
}