package com.portfolio.manager.service;

import com.portfolio.manager.entity.Holding;
import com.portfolio.manager.entity.Portfolio;
import com.portfolio.manager.dto.response.PortfolioSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioAnalyticsServiceTest {

    @Mock
    private HoldingService holdingService;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private YahooFinanceService yahooFinanceService;

    @InjectMocks
    private PortfolioAnalyticsService analyticsService;

    private Portfolio portfolio;
    private Holding holding;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Test Portfolio");
        portfolio.setCashBalance(new BigDecimal("100000.00"));

        holding = new Holding();
        holding.setId(1L);
        holding.setPortfolio(portfolio);
        holding.setTicker("AAPL");
        holding.setQuantity(new BigDecimal("10.0000"));
        holding.setAvgBuyPrice(new BigDecimal("150.0000"));
    }

    @Test
    void testGetPortfolioSummary_NoHoldings() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(holdingService.getAllHoldingEntities()).thenReturn(Collections.emptyList());

        PortfolioSummaryResponse result = analyticsService.getPortfolioSummary();

        assertNotNull(result);
        assertEquals(new BigDecimal("100000.00"), result.getCashBalance());
        assertEquals(BigDecimal.ZERO, result.getTotalInvested());
        assertEquals(BigDecimal.ZERO, result.getCurrentValue());
        assertEquals(BigDecimal.ZERO, result.getProfitLoss());
    }

    @Test
    void testGetPortfolioSummary_WithHoldings() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(holdingService.getAllHoldingEntities()).thenReturn(Arrays.asList(holding));
        when(yahooFinanceService.getPrice("AAPL")).thenReturn(new BigDecimal("180.00"));

        PortfolioSummaryResponse result = analyticsService.getPortfolioSummary();

        assertNotNull(result);
        // invested = 10 * 150 = 1500
        assertEquals(new BigDecimal("1500.00"), result.getTotalInvested());
        // current = 10 * 180 = 1800
        assertEquals(new BigDecimal("1800.00"), result.getCurrentValue());
        // profit = 1800 - 1500 = 300
        assertEquals(new BigDecimal("300.00"), result.getProfitLoss());
        // return = 300 / 1500 * 100 = 20%
        assertEquals(new BigDecimal("20.00"), result.getReturnPercent());
    }

    @Test
    void testGetPortfolioSummary_NegativeReturn() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(holdingService.getAllHoldingEntities()).thenReturn(Arrays.asList(holding));
        when(yahooFinanceService.getPrice("AAPL")).thenReturn(new BigDecimal("120.00"));

        PortfolioSummaryResponse result = analyticsService.getPortfolioSummary();

        assertNotNull(result);
        // invested = 10 * 150 = 1500
        assertEquals(new BigDecimal("1500.00"), result.getTotalInvested());
        // current = 10 * 120 = 1200
        assertEquals(new BigDecimal("1200.00"), result.getCurrentValue());
        // profit = 1200 - 1500 = -300
        assertEquals(new BigDecimal("-300.00"), result.getProfitLoss());
        // return = -300 / 1500 * 100 = -20%
        assertEquals(new BigDecimal("-20.00"), result.getReturnPercent());
    }

    @Test
    void testGetPortfolioSummary_MultipleHoldings() {
        Holding holding2 = new Holding();
        holding2.setId(2L);
        holding2.setPortfolio(portfolio);
        holding2.setTicker("NVDA");
        holding2.setQuantity(new BigDecimal("5.0000"));
        holding2.setAvgBuyPrice(new BigDecimal("500.0000"));

        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(holdingService.getAllHoldingEntities()).thenReturn(Arrays.asList(holding, holding2));
        when(yahooFinanceService.getPrice("AAPL")).thenReturn(new BigDecimal("200.00"));
        when(yahooFinanceService.getPrice("NVDA")).thenReturn(new BigDecimal("600.00"));

        PortfolioSummaryResponse result = analyticsService.getPortfolioSummary();

        assertNotNull(result);
        // AAPL: invested = 10 * 150 = 1500, current = 10 * 200 = 2000
        // NVDA: invested = 5 * 500 = 2500, current = 5 * 600 = 3000
        // Total invested = 4000, current = 5000
        assertEquals(new BigDecimal("4000.00"), result.getTotalInvested());
        assertEquals(new BigDecimal("5000.00"), result.getCurrentValue());
        assertEquals(new BigDecimal("1000.00"), result.getProfitLoss());
        // return = 1000 / 4000 * 100 = 25%
        assertEquals(new BigDecimal("25.00"), result.getReturnPercent());
    }
}
