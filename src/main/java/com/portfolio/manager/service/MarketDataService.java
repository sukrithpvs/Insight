package com.portfolio.manager.service;

import com.portfolio.manager.dto.response.MarketMoverResponse;
import com.portfolio.manager.dto.response.StockDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class MarketDataService {

    // Popular Indian stocks for demo
    private static final List<String> POPULAR_STOCKS = Arrays.asList(
            "RELIANCE.NS", "TCS.NS", "HDFCBANK.NS", "INFY.NS", "ICICIBANK.NS",
            "HINDUNILVR.NS", "SBIN.NS", "BHARTIARTL.NS", "KOTAKBANK.NS", "LT.NS");

    // US stocks for demo
    private static final List<String> US_STOCKS = Arrays.asList(
            "AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "META", "NVDA", "JPM", "V", "WMT");

    // Mock data for when Yahoo Finance is unavailable
    private static final Map<String, MockStockData> MOCK_STOCKS = new HashMap<>();

    static {
        MOCK_STOCKS.put("AAPL", new MockStockData("Apple Inc.", 182.50, 5.2, 2.85e12, 28.5));
        MOCK_STOCKS.put("MSFT", new MockStockData("Microsoft Corp.", 378.90, 3.8, 2.81e12, 35.2));
        MOCK_STOCKS.put("GOOGL", new MockStockData("Alphabet Inc.", 141.80, 4.1, 1.78e12, 25.8));
        MOCK_STOCKS.put("AMZN", new MockStockData("Amazon.com Inc.", 178.25, 6.2, 1.86e12, 62.3));
        MOCK_STOCKS.put("TSLA", new MockStockData("Tesla Inc.", 248.75, -2.4, 790e9, 72.5));
        MOCK_STOCKS.put("META", new MockStockData("Meta Platforms", 485.60, 4.8, 1.24e12, 32.1));
        MOCK_STOCKS.put("NVDA", new MockStockData("NVIDIA Corp.", 682.35, 7.2, 1.68e12, 65.4));
        MOCK_STOCKS.put("JPM", new MockStockData("JPMorgan Chase", 195.40, 1.5, 562e9, 11.2));
        MOCK_STOCKS.put("V", new MockStockData("Visa Inc.", 275.20, 2.1, 565e9, 29.8));
        MOCK_STOCKS.put("WMT", new MockStockData("Walmart Inc.", 165.80, 0.8, 446e9, 28.4));
        MOCK_STOCKS.put("NFLX", new MockStockData("Netflix Inc.", 545.20, 3.5, 236e9, 42.3));
        MOCK_STOCKS.put("DIS", new MockStockData("Walt Disney Co.", 112.45, -1.2, 205e9, 68.5));
    }

    public StockDetailResponse getStockDetail(String ticker) {
        String upperTicker = ticker.toUpperCase();
        log.info("Fetching detailed stock data for: {}", upperTicker);

        try {
            Stock stock = YahooFinance.get(upperTicker);
            if (stock != null && stock.getQuote() != null) {
                return buildFromYahoo(stock);
            }
        } catch (Exception e) {
            log.warn("Yahoo Finance unavailable for {}, using mock data: {}", upperTicker, e.getMessage());
        }

        return buildMockResponse(upperTicker);
    }

    private StockDetailResponse buildFromYahoo(Stock stock) {
        StockQuote quote = stock.getQuote();

        return StockDetailResponse.builder()
                .ticker(stock.getSymbol())
                .name(stock.getName())
                .exchange(stock.getStockExchange())
                .currency(stock.getCurrency())
                .price(quote.getPrice())
                .open(quote.getOpen())
                .high(quote.getDayHigh())
                .low(quote.getDayLow())
                .previousClose(quote.getPreviousClose())
                .volume(quote.getVolume())
                .avgVolume(quote.getAvgVolume())
                .change(quote.getChange())
                .changePercent(quote.getChangeInPercent())
                .marketCap(stock.getStats() != null ? stock.getStats().getMarketCap() : null)
                .peRatio(stock.getStats() != null ? stock.getStats().getPe() : null)
                .eps(stock.getStats() != null ? stock.getStats().getEps() : null)
                .fiftyTwoWeekHigh(quote.getYearHigh())
                .fiftyTwoWeekLow(quote.getYearLow())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    private StockDetailResponse buildMockResponse(String ticker) {
        MockStockData mock = MOCK_STOCKS.getOrDefault(ticker,
                new MockStockData(ticker, 100 + Math.random() * 200, Math.random() * 10 - 5, 100e9, 25.0));

        double price = mock.price * (1 + (Math.random() - 0.5) * 0.02); // Small variation
        double change = price * mock.changePercent / 100;

        return StockDetailResponse.builder()
                .ticker(ticker)
                .name(mock.name)
                .exchange("NASDAQ")
                .currency("USD")
                .price(BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP))
                .open(BigDecimal.valueOf(price * 0.99).setScale(2, RoundingMode.HALF_UP))
                .high(BigDecimal.valueOf(price * 1.02).setScale(2, RoundingMode.HALF_UP))
                .low(BigDecimal.valueOf(price * 0.98).setScale(2, RoundingMode.HALF_UP))
                .previousClose(BigDecimal.valueOf(price - change).setScale(2, RoundingMode.HALF_UP))
                .volume((long) (Math.random() * 50000000))
                .avgVolume((long) (Math.random() * 30000000))
                .change(BigDecimal.valueOf(change).setScale(2, RoundingMode.HALF_UP))
                .changePercent(BigDecimal.valueOf(mock.changePercent).setScale(2, RoundingMode.HALF_UP))
                .marketCap(BigDecimal.valueOf(mock.marketCap))
                .peRatio(BigDecimal.valueOf(mock.peRatio).setScale(2, RoundingMode.HALF_UP))
                .eps(BigDecimal.valueOf(price / mock.peRatio).setScale(2, RoundingMode.HALF_UP))
                .fiftyTwoWeekHigh(BigDecimal.valueOf(price * 1.3).setScale(2, RoundingMode.HALF_UP))
                .fiftyTwoWeekLow(BigDecimal.valueOf(price * 0.7).setScale(2, RoundingMode.HALF_UP))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public List<MarketMoverResponse> getTopGainers() {
        log.info("Fetching top gainers");
        List<MarketMoverResponse> gainers = new ArrayList<>();

        for (String ticker : US_STOCKS) {
            try {
                StockDetailResponse stock = getStockDetail(ticker);
                if (stock.getChangePercent() != null && stock.getChangePercent().compareTo(BigDecimal.ZERO) > 0) {
                    gainers.add(MarketMoverResponse.builder()
                            .ticker(stock.getTicker())
                            .name(stock.getName())
                            .price(stock.getPrice())
                            .change(stock.getChange())
                            .changePercent(stock.getChangePercent())
                            .volume(stock.getVolume())
                            .marketCap(stock.getMarketCap())
                            .build());
                }
            } catch (Exception e) {
                log.warn("Error fetching {}: {}", ticker, e.getMessage());
            }
        }

        // Sort by change percent descending
        gainers.sort((a, b) -> b.getChangePercent().compareTo(a.getChangePercent()));
        return gainers.stream().limit(5).toList();
    }

    public List<MarketMoverResponse> getTopLosers() {
        log.info("Fetching top losers");
        List<MarketMoverResponse> losers = new ArrayList<>();

        for (String ticker : US_STOCKS) {
            try {
                StockDetailResponse stock = getStockDetail(ticker);
                if (stock.getChangePercent() != null && stock.getChangePercent().compareTo(BigDecimal.ZERO) < 0) {
                    losers.add(MarketMoverResponse.builder()
                            .ticker(stock.getTicker())
                            .name(stock.getName())
                            .price(stock.getPrice())
                            .change(stock.getChange())
                            .changePercent(stock.getChangePercent())
                            .volume(stock.getVolume())
                            .marketCap(stock.getMarketCap())
                            .build());
                }
            } catch (Exception e) {
                log.warn("Error fetching {}: {}", ticker, e.getMessage());
            }
        }

        // Sort by change percent ascending (most negative first)
        losers.sort(Comparator.comparing(MarketMoverResponse::getChangePercent));
        return losers.stream().limit(5).toList();
    }

    public List<MarketMoverResponse> getMostActive() {
        log.info("Fetching most active stocks");
        List<MarketMoverResponse> active = new ArrayList<>();

        for (String ticker : US_STOCKS) {
            try {
                StockDetailResponse stock = getStockDetail(ticker);
                active.add(MarketMoverResponse.builder()
                        .ticker(stock.getTicker())
                        .name(stock.getName())
                        .price(stock.getPrice())
                        .change(stock.getChange())
                        .changePercent(stock.getChangePercent())
                        .volume(stock.getVolume())
                        .marketCap(stock.getMarketCap())
                        .build());
            } catch (Exception e) {
                log.warn("Error fetching {}: {}", ticker, e.getMessage());
            }
        }

        // Sort by volume descending
        active.sort((a, b) -> Long.compare(b.getVolume() != null ? b.getVolume() : 0,
                a.getVolume() != null ? a.getVolume() : 0));
        return active.stream().limit(5).toList();
    }

    // Inner class for mock data
    private static class MockStockData {
        String name;
        double price;
        double changePercent;
        double marketCap;
        double peRatio;

        MockStockData(String name, double price, double changePercent, double marketCap, double peRatio) {
            this.name = name;
            this.price = price;
            this.changePercent = changePercent;
            this.marketCap = marketCap;
            this.peRatio = peRatio;
        }
    }
}
