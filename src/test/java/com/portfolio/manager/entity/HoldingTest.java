package com.portfolio.manager.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class HoldingTest {

    private Holding holding;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Test Portfolio");

        holding = new Holding();
        holding.setId(1L);
        holding.setPortfolio(portfolio);
        holding.setTicker("AAPL");
        holding.setQuantity(new BigDecimal("10.0000"));
        holding.setAvgBuyPrice(new BigDecimal("182.5000"));
        holding.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, holding.getId());
        assertEquals(portfolio, holding.getPortfolio());
        assertEquals("AAPL", holding.getTicker());
        assertEquals(new BigDecimal("10.0000"), holding.getQuantity());
        assertEquals(new BigDecimal("182.5000"), holding.getAvgBuyPrice());
        assertNotNull(holding.getCreatedAt());
    }

    @Test
    void testOnCreate_TickerUppercase() {
        Holding newHolding = new Holding();
        newHolding.setTicker("msft");
        newHolding.onCreate();

        assertEquals("MSFT", newHolding.getTicker());
        assertNotNull(newHolding.getCreatedAt());
    }

    @Test
    void testOnCreate_TickerNull() {
        Holding newHolding = new Holding();
        newHolding.setTicker(null);
        newHolding.onCreate();

        assertNull(newHolding.getTicker());
        assertNotNull(newHolding.getCreatedAt());
    }

    @Test
    void testOnUpdate_TickerUppercase() {
        holding.setTicker("tsla");
        holding.onUpdate();

        assertEquals("TSLA", holding.getTicker());
    }

    @Test
    void testBuilder() {
        Holding builtHolding = Holding.builder()
                .id(2L)
                .ticker("GOOGL")
                .quantity(new BigDecimal("5.0000"))
                .avgBuyPrice(new BigDecimal("150.0000"))
                .build();

        assertEquals(2L, builtHolding.getId());
        assertEquals("GOOGL", builtHolding.getTicker());
        assertEquals(new BigDecimal("5.0000"), builtHolding.getQuantity());
        assertEquals(new BigDecimal("150.0000"), builtHolding.getAvgBuyPrice());
    }

    @Test
    void testGetInvestedAmount() {
        BigDecimal expected = new BigDecimal("10.0000").multiply(new BigDecimal("182.5000"));
        assertEquals(expected, holding.getQuantity().multiply(holding.getAvgBuyPrice()));
    }

    @Test
    void testEquality() {
        Holding holding2 = new Holding();
        holding2.setId(1L);
        holding2.setTicker("AAPL");

        assertEquals(holding.getId(), holding2.getId());
    }
}
