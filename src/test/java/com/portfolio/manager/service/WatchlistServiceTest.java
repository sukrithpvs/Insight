package com.portfolio.manager.service;

import com.portfolio.manager.entity.Portfolio;
import com.portfolio.manager.entity.WatchlistItem;
import com.portfolio.manager.repository.WatchlistRepository;
import com.portfolio.manager.dto.request.AddToWatchlistRequest;
import com.portfolio.manager.dto.response.PriceResponse;
import com.portfolio.manager.dto.response.WatchlistItemResponse;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @Mock
    private WatchlistRepository watchlistRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private YahooFinanceService yahooFinanceService;

    @Mock
    private MarketDataService marketDataService;

    @InjectMocks
    private WatchlistService watchlistService;

    private Portfolio portfolio;
    private WatchlistItem item;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Test Portfolio");

        item = new WatchlistItem();
        item.setId(1L);
        item.setPortfolio(portfolio);
        item.setTicker("NVDA");
        item.setCompanyName("NVIDIA Corp.");
        item.setAddedAt(LocalDateTime.now());
        item.setNotes("Watch for dip");
    }

    @Test
    void testGetWatchlist() {
        PriceResponse priceResponse = PriceResponse.builder()
                .ticker("NVDA")
                .price(new BigDecimal("850.00"))
                .build();

        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(watchlistRepository.findByPortfolioOrderByAddedAtDesc(portfolio)).thenReturn(Arrays.asList(item));
        when(yahooFinanceService.getStockPrice("NVDA")).thenReturn(priceResponse);

        List<WatchlistItemResponse> result = watchlistService.getWatchlist();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("NVDA", result.get(0).getTicker());
    }

    @Test
    void testGetWatchlist_Empty() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(watchlistRepository.findByPortfolioOrderByAddedAtDesc(portfolio)).thenReturn(Arrays.asList());

        List<WatchlistItemResponse> result = watchlistService.getWatchlist();

        assertEquals(0, result.size());
    }

    @Test
    void testRemoveFromWatchlist_Success() {
        when(watchlistRepository.existsById(1L)).thenReturn(true);
        doNothing().when(watchlistRepository).deleteById(1L);

        assertDoesNotThrow(() -> watchlistService.removeFromWatchlist(1L));
        verify(watchlistRepository).deleteById(1L);
    }

    @Test
    void testRemoveFromWatchlist_NotFound() {
        when(watchlistRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> watchlistService.removeFromWatchlist(99L));
    }

    @Test
    void testRemoveByTicker_Success() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(watchlistRepository.findByPortfolioAndTicker(portfolio, "NVDA")).thenReturn(Optional.of(item));
        doNothing().when(watchlistRepository).delete(item);

        assertDoesNotThrow(() -> watchlistService.removeByTicker("NVDA"));
        verify(watchlistRepository).delete(item);
    }

    @Test
    void testRemoveByTicker_NotFound() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(watchlistRepository.findByPortfolioAndTicker(portfolio, "AMD")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> watchlistService.removeByTicker("AMD"));
    }

    @Test
    void testRemoveByTicker_LowercaseTicker() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(watchlistRepository.findByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(Optional.of(item));
        doNothing().when(watchlistRepository).delete(item);

        assertDoesNotThrow(() -> watchlistService.removeByTicker("aapl"));
        verify(watchlistRepository).findByPortfolioAndTicker(portfolio, "AAPL");
    }
}
