package com.argusoft.path.tht.systemconfiguration.cache.config;

import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager();

        // Optionally configure the cacheManager
        cacheManager.createCache("authenticationCache", new MutableConfiguration<>());

        return new JCacheCacheManager(cacheManager);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernateSecondLevelCacheCustomizer() {
        return hibernateProperties -> {
            hibernateProperties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory");
            hibernateProperties.put("hibernate.cache.use_query_cache", true);
            hibernateProperties.put("hibernate.cache.use_second_level_cache", true);
        };
    }
}