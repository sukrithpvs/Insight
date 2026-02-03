package com.portfolio.manager.service;

import com.portfolio.manager.dto.request.AddToWatchlistRequest;
import com.portfolio.manager.dto.response.PriceResponse;
import com.portfolio.manager.dto.response.WatchlistItemResponse;
import com.portfolio.manager.entity.Portfolio;
import com.portfolio.manager.entity.WatchlistItem;
import com.portfolio.manager.exception.BadRequestException;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final PortfolioService portfolioService;
    private final YahooFinanceService yahooFinanceService;
    private final MarketDataService marketDataService;

    @Transactional
    public WatchlistItemResponse addToWatchlist(AddToWatchlistRequest request) {
        String ticker = request.getTicker().toUpperCase();
        log.info("Adding {} to watchlist", ticker);

        Portfolio portfolio = portfolioService.getPortfolioEntity();

        if (watchlistRepository.existsByPortfolioAndTicker(portfolio, ticker)) {
            throw new BadRequestException(ticker + " is already in your watchlist");
        }

        // Get stock name from Yahoo Finance
        String companyName = ticker;
        try {
            var stockDetail = marketDataService.getStockDetail(ticker);
            if (stockDetail.getName() != null) {
                companyName = stockDetail.getName();
            }
        } catch (Exception e) {
            log.warn("Could not fetch company name for {}", ticker);
        }

        WatchlistItem item = WatchlistItem.builder()
                .portfolio(portfolio)
                .ticker(ticker)
                .companyName(companyName)
                .notes(request.getNotes())
                .build();

        WatchlistItem saved = watchlistRepository.save(item);
        log.info("Added {} to watchlist", ticker);

        return enrichWithPrice(saved);
    }

    @Transactional(readOnly = true)
    public List<WatchlistItemResponse> getWatchlist() {
        Portfolio portfolio = portfolioService.getPortfolioEntity();
        return watchlistRepository.findByPortfolioOrderByAddedAtDesc(portfolio).stream()
                .map(this::enrichWithPrice)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeFromWatchlist(Long id) {
        if (!watchlistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Watchlist item", "id", id);
        }
        watchlistRepository.deleteById(id);
        log.info("Removed item {} from watchlist", id);
    }

    @Transactional
    public void removeByTicker(String ticker) {
        Portfolio portfolio = portfolioService.getPortfolioEntity();
        WatchlistItem item = watchlistRepository.findByPortfolioAndTicker(portfolio, ticker.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist item", "ticker", ticker));
        watchlistRepository.delete(item);
        log.info("Removed {} from watchlist", ticker);
    }

    private WatchlistItemResponse enrichWithPrice(WatchlistItem item) {
        BigDecimal currentPrice = BigDecimal.ZERO;
        BigDecimal changePercent = BigDecimal.ZERO;

        try {
            PriceResponse priceResponse = yahooFinanceService.getStockPrice(item.getTicker());
            currentPrice = priceResponse.getPrice();
            // Mock change percent for now
            changePercent = BigDecimal.valueOf(Math.random() * 10 - 5)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.warn("Could not fetch price for {}", item.getTicker());
        }

        return WatchlistItemResponse.builder()
                .id(item.getId())
                .ticker(item.getTicker())
                .companyName(item.getCompanyName())
                .currentPrice(currentPrice)
                .changePercent(changePercent)
                .addedAt(item.getAddedAt())
                .notes(item.getNotes())
                .build();
    }
}
