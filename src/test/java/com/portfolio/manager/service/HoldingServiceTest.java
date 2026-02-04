package com.portfolio.manager.service;

import com.portfolio.manager.entity.Holding;
import com.portfolio.manager.entity.Portfolio;
import com.portfolio.manager.repository.HoldingRepository;
import com.portfolio.manager.dto.request.AddHoldingRequest;
import com.portfolio.manager.dto.response.HoldingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoldingServiceTest {

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private HoldingService holdingService;

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
        holding.setAvgBuyPrice(new BigDecimal("182.5000"));
        holding.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllHoldings() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(holdingRepository.findByPortfolio(portfolio)).thenReturn(Arrays.asList(holding));

        List<HoldingResponse> result = holdingService.getAllHoldings();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
        verify(holdingRepository).findByPortfolio(portfolio);
    }

    @Test
    void testGetAllHoldings_Empty() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(holdingRepository.findByPortfolio(portfolio)).thenReturn(Arrays.asList());

        List<HoldingResponse> result = holdingService.getAllHoldings();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testAddHolding_Success() {
        AddHoldingRequest request = new AddHoldingRequest();
        request.setTicker("AAPL");
        request.setQuantity(new BigDecimal("10.0000"));
        request.setAvgBuyPrice(new BigDecimal("182.5000"));

        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(holdingRepository.existsByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(false);
        when(holdingRepository.save(any(Holding.class))).thenAnswer(i -> {
            Holding h = i.getArgument(0);
            h.setId(1L);
            return h;
        });

        HoldingResponse result = holdingService.addHolding(request);

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        verify(holdingRepository).save(any(Holding.class));
    }

    @Test
    void testAddHolding_AlreadyExists() {
        AddHoldingRequest request = new AddHoldingRequest();
        request.setTicker("AAPL");
        request.setQuantity(new BigDecimal("10.0000"));
        request.setAvgBuyPrice(new BigDecimal("182.5000"));

        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(holdingRepository.existsByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(true);

        assertThrows(Exception.class, () -> holdingService.addHolding(request));
    }

    @Test
    void testDeleteHolding_Success() {
        when(holdingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(holdingRepository).deleteById(1L);

        assertDoesNotThrow(() -> holdingService.deleteHolding(1L));
        verify(holdingRepository).deleteById(1L);
    }

    @Test
    void testDeleteHolding_NotFound() {
        when(holdingRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> holdingService.deleteHolding(99L));
    }

    @Test
    void testGetAllHoldingEntities() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(holdingRepository.findByPortfolio(portfolio)).thenReturn(Arrays.asList(holding));

        List<Holding> result = holdingService.getAllHoldingEntities();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
    }
}
