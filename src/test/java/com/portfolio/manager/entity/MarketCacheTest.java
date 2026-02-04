package com.portfolio.manager.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class MarketCacheTest {

    private MarketCache cache;

    @BeforeEach
    void setUp() {
        cache = new MarketCache();
        cache.setId(1L);
        cache.setCacheKey("top_gainers");
        cache.setCacheValue("[{\"ticker\":\"AAPL\",\"price\":182.50}]");
        cache.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, cache.getId());
        assertEquals("top_gainers", cache.getCacheKey());
        assertNotNull(cache.getCacheValue());
        assertNotNull(cache.getUpdatedAt());
    }

    @Test
    void testIsExpired_NotExpired() {
        cache.setUpdatedAt(LocalDateTime.now());
        assertFalse(cache.isExpired(300)); // 5 hours TTL
    }

    @Test
    void testIsExpired_Expired() {
        cache.setUpdatedAt(LocalDateTime.now().minusMinutes(400));
        assertTrue(cache.isExpired(300)); // 5 hours TTL expired
    }

    @Test
    void testIsExpired_NullUpdatedAt() {
        cache.setUpdatedAt(null);
        assertTrue(cache.isExpired(300));
    }

    @Test
    void testIsExpired_JustExpired() {
        cache.setUpdatedAt(LocalDateTime.now().minusMinutes(301));
        assertTrue(cache.isExpired(300));
    }

    @Test
    void testIsExpired_JustNotExpired() {
        cache.setUpdatedAt(LocalDateTime.now().minusMinutes(299));
        assertFalse(cache.isExpired(300));
    }

    @Test
    void testBuilder() {
        MarketCache builtCache = MarketCache.builder()
                .id(2L)
                .cacheKey("market_news")
                .cacheValue("[{\"title\":\"News 1\"}]")
                .updatedAt(LocalDateTime.now())
                .build();

        assertEquals(2L, builtCache.getId());
        assertEquals("market_news", builtCache.getCacheKey());
    }

    @Test
    void testCacheValueJsonString() {
        String jsonValue = "[{\"ticker\":\"NVDA\",\"price\":682.35,\"change\":28.42}]";
        cache.setCacheValue(jsonValue);
        assertEquals(jsonValue, cache.getCacheValue());
    }
}
