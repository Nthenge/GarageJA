package com.eclectics.Garage.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheStatsLogger {

    private static final Logger logger = LoggerFactory.getLogger(CacheStatsLogger.class);
    private final CacheManager cacheManager;

    public CacheStatsLogger(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedRate = 60000)
    public void logCacheStats() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Object nativeCache = cacheManager.getCache(cacheName).getNativeCache();
            if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache) {
                var stats = caffeineCache.stats();
                logger.info("[CACHE STATS] {} → hits={}, misses={}, evictions={}, loadTime={}ns",
                        cacheName, stats.hitCount(), stats.missCount(), stats.evictionCount(), stats.totalLoadTime());
            }
        });
    }
}

