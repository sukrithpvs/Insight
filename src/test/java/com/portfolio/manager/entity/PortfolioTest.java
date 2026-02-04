package com.portfolio.manager.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

class PortfolioTest {

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Test Portfolio");
        portfolio.setCashBalance(new BigDecimal("100000.00"));
        portfolio.setHoldings(new ArrayList<>());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, portfolio.getId());
        assertEquals("Test Portfolio", portfolio.getName());
        assertEquals(new BigDecimal("100000.00"), portfolio.getCashBalance());
        assertNotNull(portfolio.getHoldings());
    }

    @Test
    void testGetCashBalanceSafe_WithValue() {
        assertEquals(new BigDecimal("100000.00"), portfolio.getCashBalanceSafe());
    }

    @Test
    void testGetCashBalanceSafe_NullValue() {
        portfolio.setCashBalance(null);
        assertEquals(new BigDecimal("100000.00"), portfolio.getCashBalanceSafe());
    }

    @Test
    void testInitializeCashBalanceIfNull() {
        portfolio.setCashBalance(null);
        portfolio.initializeCashBalanceIfNull();
        assertEquals(new BigDecimal("100000.00"), portfolio.getCashBalance());
    }

    @Test
    void testInitializeCashBalanceIfNull_AlreadySet() {
        portfolio.setCashBalance(new BigDecimal("50000.00"));
        portfolio.initializeCashBalanceIfNull();
        assertEquals(new BigDecimal("50000.00"), portfolio.getCashBalance());
    }

    @Test
    void testAddHolding() {
        Holding holding = new Holding();
        holding.setTicker("AAPL");
        portfolio.addHolding(holding);

        assertEquals(1, portfolio.getHoldings().size());
        assertEquals(portfolio, holding.getPortfolio());
    }

    @Test
    void testRemoveHolding() {
        Holding holding = new Holding();
        holding.setTicker("AAPL");
        portfolio.addHolding(holding);
        portfolio.removeHolding(holding);

        assertEquals(0, portfolio.getHoldings().size());
        assertNull(holding.getPortfolio());
    }

    @Test
    void testDefaultConstructor() {
        Portfolio defaultPortfolio = new Portfolio();
        assertNull(defaultPortfolio.getId());
        assertNull(defaultPortfolio.getName());
    }

    @Test
    void testBuilder() {
        Portfolio builtPortfolio = Portfolio.builder()
                .id(2L)
                .name("Built Portfolio")
                .cashBalance(new BigDecimal("50000.00"))
                .build();

        assertEquals(2L, builtPortfolio.getId());
        assertEquals("Built Portfolio", builtPortfolio.getName());
        assertEquals(new BigDecimal("50000.00"), builtPortfolio.getCashBalance());
    }
}
