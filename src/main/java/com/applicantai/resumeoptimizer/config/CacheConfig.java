package com.applicantai.resumeoptimizer.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for Caffeine caching.
 * Sets up caching for resume processing to improve performance for repeated uploads.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates a Caffeine cache manager with specific configurations.
     * 
     * @return configured CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        cacheManager.setCacheNames(java.util.Arrays.asList("resumeCache"));
        return cacheManager;
    }
    
    /**
     * Configure Caffeine cache properties.
     * 
     * @return Caffeine instance with configured properties
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(100)             // Maximum number of entries in the cache
                .expireAfterWrite(30, TimeUnit.MINUTES)  // Cache entries expire after 30 minutes
                .recordStats();                // Enable statistics
    }
} 