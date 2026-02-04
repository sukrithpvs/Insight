package com.portfolio.manager.service;

import com.portfolio.manager.entity.Portfolio;
import com.portfolio.manager.dto.request.CreatePortfolioRequest;
import com.portfolio.manager.dto.response.PortfolioResponse;
import com.portfolio.manager.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

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
    void testCreatePortfolio() {
        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setName("My Portfolio");

        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(i -> {
            Portfolio p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        PortfolioResponse result = portfolioService.createPortfolio(request);

        assertNotNull(result);
        assertEquals("My Portfolio", result.getName());
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void testGetPortfolio_Exists() {
        when(portfolioRepository.findAll()).thenReturn(Arrays.asList(portfolio));

        PortfolioResponse result = portfolioService.getPortfolio();

        assertNotNull(result);
        assertEquals("Test Portfolio", result.getName());
    }

    @Test
    void testGetPortfolio_NotFound() {
        when(portfolioRepository.findAll()).thenReturn(Arrays.asList());

        assertThrows(RuntimeException.class, () -> portfolioService.getPortfolio());
    }

    @Test
    void testGetPortfolioEntity_Exists() {
        when(portfolioRepository.findAll()).thenReturn(Arrays.asList(portfolio));

        Portfolio result = portfolioService.getPortfolioEntity();

        assertNotNull(result);
        assertEquals("Test Portfolio", result.getName());
        assertEquals(new BigDecimal("100000.00"), result.getCashBalance());
    }

    @Test
    void testGetPortfolioEntity_NullCashBalance() {
        portfolio.setCashBalance(null);
        when(portfolioRepository.findAll()).thenReturn(Arrays.asList(portfolio));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);

        Portfolio result = portfolioService.getPortfolioEntity();

        assertNotNull(result);
        // The method should initialize cash balance
        verify(portfolioRepository).save(portfolio);
    }

    @Test
    void testHasPortfolio_True() {
        when(portfolioRepository.count()).thenReturn(1L);

        assertTrue(portfolioService.hasPortfolio());
    }

    @Test
    void testHasPortfolio_False() {
        when(portfolioRepository.count()).thenReturn(0L);

        assertFalse(portfolioService.hasPortfolio());
    }
}
