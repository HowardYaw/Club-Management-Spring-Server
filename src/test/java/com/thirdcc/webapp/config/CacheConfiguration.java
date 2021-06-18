package com.thirdcc.webapp.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;

public class CacheConfiguration {
    @Bean
    public CacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}
