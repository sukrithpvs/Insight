package com.portfolio.manager.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class WatchlistItemTest {

    private WatchlistItem item;

    @BeforeEach
    void setUp() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);

        item = new WatchlistItem();
        item.setId(1L);
        item.setPortfolio(portfolio);
        item.setTicker("NVDA");
        item.setCompanyName("NVIDIA Corp.");
        item.setAddedAt(LocalDateTime.now());
        item.setNotes("Watch for dip");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, item.getId());
        assertNotNull(item.getPortfolio());
        assertEquals("NVDA", item.getTicker());
        assertEquals("NVIDIA Corp.", item.getCompanyName());
        assertNotNull(item.getAddedAt());
        assertEquals("Watch for dip", item.getNotes());
    }

    @Test
    void testBuilder() {
        WatchlistItem builtItem = WatchlistItem.builder()
                .id(2L)
                .ticker("AMD")
                .companyName("AMD Inc.")
                .notes("Good for long term")
                .addedAt(LocalDateTime.now())
                .build();

        assertEquals(2L, builtItem.getId());
        assertEquals("AMD", builtItem.getTicker());
        assertEquals("AMD Inc.", builtItem.getCompanyName());
        assertEquals("Good for long term", builtItem.getNotes());
    }

    @Test
    void testOnCreate() {
        WatchlistItem newItem = new WatchlistItem();
        newItem.setTicker("tsla");
        newItem.onCreate();

        assertEquals("TSLA", newItem.getTicker());
        assertNotNull(newItem.getAddedAt());
    }

    @Test
    void testOnCreate_NullTicker() {
        WatchlistItem newItem = new WatchlistItem();
        newItem.onCreate();

        assertNull(newItem.getTicker());
        assertNotNull(newItem.getAddedAt());
    }

    @Test
    void testNullNotes() {
        item.setNotes(null);
        assertNull(item.getNotes());
    }

    @Test
    void testEmptyNotes() {
        item.setNotes("");
        assertEquals("", item.getNotes());
    }
}
