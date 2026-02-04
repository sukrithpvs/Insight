# 💻 Backend Code Documentation

## Project Structure

```
src/main/java/com/portfolio/manager/
├── PortfolioManagerApplication.java      # Spring Boot Entry Point
├── config/                                # Configuration Classes
│   ├── CorsConfig.java
│   ├── SwaggerConfig.java
│   └── WebConfig.java
├── controller/                            # REST Controllers (6 files)
│   ├── HoldingController.java
│   ├── MarketDataController.java
│   ├── OrderController.java
│   ├── PortfolioController.java
│   ├── PriceController.java
│   └── WatchlistController.java
├── dto/                                   # Data Transfer Objects (15 files)
│   ├── request/
│   └── response/
├── entity/                                # JPA Entities (5 files)
│   ├── Holding.java
│   ├── MarketCache.java
│   ├── Order.java
│   ├── Portfolio.java
│   └── WatchlistItem.java
├── exception/                             # Exception Handlers (3 files)
│   ├── GlobalExceptionHandler.java
│   ├── InsufficientFundsException.java
│   └── ResourceNotFoundException.java
├── repository/                            # JPA Repositories (5 files)
│   ├── HoldingRepository.java
│   ├── MarketCacheRepository.java
│   ├── OrderRepository.java
│   ├── PortfolioRepository.java
│   └── WatchlistRepository.java
└── service/                               # Business Logic (10 files)
    ├── FinnhubService.java
    ├── HoldingService.java
    ├── MarketDataService.java
    ├── MutualFundService.java
    ├── NewsService.java
    ├── OrderService.java
    ├── PortfolioAnalyticsService.java
    ├── PortfolioService.java
    ├── WatchlistService.java
    └── YahooFinanceService.java
```

---

# SECTION 1: ENTITY CLASSES

## 1.1 Portfolio.java

```java
@Entity
@Table(name = "portfolios")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "cash_balance", precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal cashBalance = new BigDecimal("100000.00");

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<Holding> holdings = new ArrayList<>();
}
```

| Field | Type | Purpose |
|-------|------|---------|
| id | Long | Primary key, auto-increment |
| name | String | Portfolio name |
| cashBalance | BigDecimal | Available cash (default: 100000) |
| holdings | List<Holding> | One-to-many relationship |

**Methods:**
| Method | Purpose |
|--------|---------|
| `getCashBalanceSafe()` | Returns cashBalance or default if null |
| `initializeCashBalanceIfNull()` | Sets default if null |
| `addHolding(Holding)` | Adds holding to list |
| `removeHolding(Holding)` | Removes holding from list |

---

## 1.2 Holding.java

```java
@Entity
@Table(name = "holdings")
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "avg_buy_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal avgBuyPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (ticker != null) ticker = ticker.toUpperCase();
    }
}
```

---

## 1.3 Order.java

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    private String ticker;
    
    @Enumerated(EnumType.STRING)
    private OrderType orderType;  // BUY, SELL
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;   // PENDING, COMPLETED, CANCELLED
    
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
}
```

---

## 1.4 MarketCache.java

```java
@Entity
@Table(name = "market_cache")
public class MarketCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cache_key", nullable = false, unique = true)
    private String cacheKey;

    @Column(name = "cache_value", columnDefinition = "TEXT")
    private String cacheValue;  // JSON string

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Check if cache is expired
    public boolean isExpired(long ttlMinutes) {
        if (updatedAt == null) return true;
        return LocalDateTime.now().isAfter(updatedAt.plusMinutes(ttlMinutes));
    }
}
```

---

## 1.5 WatchlistItem.java

```java
@Entity
@Table(name = "watchlist_items")
public class WatchlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    private String ticker;
    private String companyName;
    private LocalDateTime addedAt;
    private String notes;
}
```

---

# SECTION 2: REPOSITORY INTERFACES

## 2.1 PortfolioRepository.java

```java
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    // Inherits: findById(), findAll(), save(), delete(), etc.
}
```

## 2.2 HoldingRepository.java

```java
@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
    
    // Find all holdings for a portfolio
    List<Holding> findByPortfolioId(Long portfolioId);
    
    // Find specific holding by ticker
    Optional<Holding> findByPortfolioIdAndTicker(Long portfolioId, String ticker);
    
    // Delete by ticker
    void deleteByPortfolioIdAndTicker(Long portfolioId, String ticker);
}
```

## 2.3 OrderRepository.java

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Get all orders sorted by date
    List<Order> findByPortfolioIdOrderByCreatedAtDesc(Long portfolioId);
}
```

## 2.4 MarketCacheRepository.java

```java
@Repository
public interface MarketCacheRepository extends JpaRepository<MarketCache, Long> {
    
    // Find cache by key
    Optional<MarketCache> findByCacheKey(String cacheKey);
}
```

## 2.5 WatchlistRepository.java

```java
@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {
    
    List<WatchlistItem> findByPortfolioId(Long portfolioId);
    
    Optional<WatchlistItem> findByPortfolioIdAndTicker(Long portfolioId, String ticker);
    
    void deleteByPortfolioIdAndTicker(Long portfolioId, String ticker);
    
    boolean existsByPortfolioIdAndTicker(Long portfolioId, String ticker);
}
```

---

# SECTION 3: SERVICE CLASSES (Detailed)

## 3.1 MarketDataService.java

**Purpose**: Core service for market data, caching, and stock information.

### Constants
```java
private static final long CACHE_TTL_MINUTES = 300;  // 5 hours
private static final String CACHE_KEY_GAINERS = "top_gainers";
private static final String CACHE_KEY_LOSERS = "top_losers";
private static final String CACHE_KEY_INDICES = "market_indices";
private static final String CACHE_KEY_TRENDING = "trending_stocks";

private static final List<String> US_STOCKS = Arrays.asList(
    "AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "META", "NVDA", "JPM", "V", "WMT",
    "NFLX", "DIS", "PYPL", "INTC", "AMD", "CRM", "UBER", "SHOP", "SQ", "COIN"
);
```

### Functions

| Function | Parameters | Returns | Purpose |
|----------|------------|---------|---------|
| `getTopGainers()` | none | `List<MarketMoverResponse>` | Returns top 5 gaining stocks from cache or API |
| `getTopLosers()` | none | `List<MarketMoverResponse>` | Returns top 5 losing stocks |
| `getMarketIndices()` | none | `List<Map<String, Object>>` | Returns S&P, NASDAQ, DOW indices |
| `getTrendingStocks()` | none | `List<MarketMoverResponse>` | Returns 8 trending stocks |
| `searchStocks(query)` | String query | `List<StockDetailResponse>` | Search stocks by ticker/name |
| `getStockDetail(ticker)` | String ticker | `StockDetailResponse` | Get stock details (Yahoo→Finnhub→Mock) |
| `getStockWithHistory(ticker)` | String ticker | `StockHistoryResponse` | Get stock + 5-year history |
| `refreshAllCaches()` | none | void | @Scheduled - refresh all caches every 5 hours |

### Function Details

#### `getTopGainers()`
```java
public List<MarketMoverResponse> getTopGainers() {
    return getCachedOrFetch("top_gainers", this::fetchTopGainers);
}
```
- Checks cache first
- If cache miss, calls `fetchTopGainers()`
- Returns top 5 stocks with positive change %

#### `getCachedOrFetch(key, fetcher)`
```java
private <T> T getCachedOrFetch(String key, Supplier<T> fetcher) {
    // 1. Query database for cached data
    Optional<MarketCache> cached = cacheRepository.findByCacheKey(key);
    
    // 2. If found and not expired (5 hour TTL)
    if (cached.isPresent() && !cached.get().isExpired(300)) {
        log.info("✅ CACHE HIT for key: {}", key);
        return objectMapper.readValue(cached.get().getCacheValue(), ...);
    }
    
    // 3. Cache miss - fetch fresh data
    log.info("⚠️ CACHE MISS for key: {}", key);
    T data = fetcher.get();
    
    // 4. Save to database
    updateCache(key, data);
    return data;
}
```

#### `getStockDetail(ticker)`
```java
public StockDetailResponse getStockDetail(String ticker) {
    // TRY 1: Yahoo Finance
    try {
        Stock stock = YahooFinance.get(ticker);
        if (stock != null) return buildDetailFromYahoo(stock);
    } catch (Exception e) { }

    // TRY 2: Finnhub (fallback)
    try {
        StockDetailResponse data = finnhubService.getStockQuote(ticker);
        if (data != null) return data;
    } catch (Exception e) { }

    // TRY 3: Mock data (last resort)
    return buildMockDetail(ticker);
}
```

#### `getStockWithHistory(ticker)`
```java
public StockHistoryResponse getStockWithHistory(String ticker) {
    String cacheKey = "stock_history_" + ticker;
    
    // Check cache for known stocks
    if (US_STOCKS.contains(ticker)) {
        Optional<MarketCache> cached = cacheRepository.findByCacheKey(cacheKey);
        if (cached.isPresent() && !cached.get().isExpired(300)) {
            return objectMapper.readValue(cached.get().getCacheValue(), ...);
        }
    }
    
    // Fetch 5-year history from Yahoo
    Calendar from = Calendar.getInstance();
    from.add(Calendar.YEAR, -5);
    Stock stock = YahooFinance.get(ticker, from, Calendar.getInstance(), Interval.DAILY);
    
    // Build response with historical data
    return buildFromYahooWithHistory(stock);
}
```

---

## 3.2 YahooFinanceService.java

**Purpose**: Fetches stock prices with caching and Finnhub fallback.

### Fields
```java
private final FinnhubService finnhubService;
private final ConcurrentHashMap<String, CachedPrice> priceCache = new ConcurrentHashMap<>();
private static final long CACHE_TTL_MS = 5 * 60 * 1000;  // 5 minutes
```

### Functions

| Function | Parameters | Returns | Purpose |
|----------|------------|---------|---------|
| `getStockPrice(ticker)` | String ticker | `PriceResponse` | Get price with 5-min in-memory cache |
| `getPrice(ticker)` | String ticker | `BigDecimal` | Simple wrapper returning just price |
| `getMockPrice(ticker)` | String ticker | `PriceResponse` | Returns mock price |

#### `getStockPrice(ticker)` - Detailed
```java
public PriceResponse getStockPrice(String ticker) {
    String upperTicker = ticker.toUpperCase();
    
    // 1. Check in-memory cache (5 min TTL)
    CachedPrice cached = priceCache.get(upperTicker);
    if (cached != null && !cached.isExpired()) {
        log.info("📦 Price cache HIT for {}", upperTicker);
        return cached.getResponse();
    }
    
    PriceResponse response = null;
    
    // 2. Try Yahoo Finance
    try {
        Stock stock = YahooFinance.get(upperTicker);
        if (stock != null && stock.getQuote().getPrice() != null) {
            response = PriceResponse.builder()
                .ticker(upperTicker)
                .price(stock.getQuote().getPrice())
                .change(stock.getQuote().getChange())
                .changePercent(stock.getQuote().getChangeInPercent())
                .build();
        }
    } catch (Exception e) {
        log.warn("Yahoo Finance failed for {}: {}", upperTicker, e.getMessage());
    }
    
    // 3. Fallback to Finnhub
    if (response == null) {
        try {
            StockDetailResponse finnhubData = finnhubService.getStockQuote(upperTicker);
            if (finnhubData != null) {
                response = PriceResponse.builder()
                    .ticker(upperTicker)
                    .price(finnhubData.getPrice())
                    .change(finnhubData.getChange())
                    .changePercent(finnhubData.getChangePercent())
                    .build();
            }
        } catch (Exception e) { }
    }
    
    // 4. Last resort: mock data
    if (response == null) {
        response = getMockPrice(upperTicker);
    }
    
    // 5. Cache and return
    priceCache.put(upperTicker, new CachedPrice(response));
    return response;
}
```

---

## 3.3 FinnhubService.java

**Purpose**: Fallback API for when Yahoo Finance is rate-limited.

### Configuration
```java
@Value("${finnhub.api-key:ct10...")
private String apiKey;

private static final String BASE_URL = "https://finnhub.io/api/v1";
```

### Functions

| Function | Parameters | Returns | Purpose |
|----------|------------|---------|---------|
| `getStockQuote(ticker)` | String ticker | `StockDetailResponse` | Get quote from Finnhub |
| `getCompanyName(ticker)` | String ticker | `String` | Get company name |

#### `getStockQuote(ticker)` - Detailed
```java
public StockDetailResponse getStockQuote(String ticker) {
    String url = BASE_URL + "/quote?symbol=" + ticker + "&token=" + apiKey;
    
    // HTTP GET request
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build();
    
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    
    // Parse JSON response
    // Response: {"c":182.50,"d":2.85,"dp":1.56,"h":183.50,"l":181.20,"o":181.80,"pc":179.65}
    JsonNode json = objectMapper.readTree(response.body());
    
    return StockDetailResponse.builder()
        .ticker(ticker)
        .price(getBigDecimal(json, "c"))         // current price
        .change(getBigDecimal(json, "d"))         // change
        .changePercent(getBigDecimal(json, "dp")) // change percent
        .open(getBigDecimal(json, "o"))           // open
        .high(getBigDecimal(json, "h"))           // high
        .low(getBigDecimal(json, "l"))            // low
        .previousClose(getBigDecimal(json, "pc")) // previous close
        .name(getCompanyName(ticker))
        .build();
}
```

---

## 3.4 NewsService.java

**Purpose**: Fetches news from Yahoo RSS and Finnhub.

### Functions

| Function | Parameters | Returns | Purpose |
|----------|------------|---------|---------|
| `getNews(forceRefresh)` | boolean | `List<Map<String, Object>>` | Get general market news |
| `getStockNews(ticker)` | String ticker | `List<Map<String, Object>>` | Get stock-specific news |
| `fetchYahooRssNews()` | none | `List<Map>` | Fetch from Yahoo RSS |
| `fetchFinnhubNews(ticker)` | String | `List<Map>` | Fetch from Finnhub |

#### `getStockNews(ticker)` - Detailed
```java
public List<Map<String, Object>> getStockNews(String ticker) {
    String cacheKey = "stock_news_" + ticker.toUpperCase();
    
    // 1. Check cache (1 hour TTL)
    Optional<MarketCache> cached = cacheRepository.findByCacheKey(cacheKey);
    if (cached.isPresent() && !cached.get().isExpired(60)) {
        return parseFromCache(cached.get());
    }
    
    // 2. Try Yahoo RSS
    String rssUrl = "https://feeds.finance.yahoo.com/rss/2.0/headline?s=" + ticker;
    List<Map<String, Object>> news = fetchYahooRssNewsForTicker(rssUrl);
    
    // 3. Fallback to Finnhub
    if (news.isEmpty()) {
        news = fetchFinnhubNews(ticker);
    }
    
    // 4. Fallback to mock
    if (news.isEmpty()) {
        news = generateMockNews(ticker);
    }
    
    // 5. Cache and return
    updateCache(cacheKey, news);
    return news;
}
```

---

## 3.5 HoldingService.java

**Purpose**: CRUD operations for stock holdings.

### Functions

| Function | Parameters | Returns | Purpose |
|----------|------------|---------|---------|
| `getAllHoldings()` | none | `List<HoldingResponse>` | Get all holdings with enriched data |
| `getHoldingByTicker(ticker)` | String | `HoldingResponse` | Get specific holding |
| `createOrUpdateHolding(request)` | `HoldingRequest` | `HoldingResponse` | Create or update holding |
| `deleteHolding(ticker)` | String | void | Delete holding |

#### `createOrUpdateHolding(request)` - Detailed
```java
public HoldingResponse createOrUpdateHolding(HoldingRequest request) {
    Portfolio portfolio = portfolioService.getOrCreatePortfolio();
    
    // Check if holding exists
    Optional<Holding> existing = holdingRepository
        .findByPortfolioIdAndTicker(portfolio.getId(), request.getTicker());
    
    Holding holding;
    if (existing.isPresent()) {
        // UPDATE: Recalculate average price
        holding = existing.get();
        BigDecimal totalShares = holding.getQuantity().add(request.getQuantity());
        BigDecimal totalCost = holding.getQuantity().multiply(holding.getAvgBuyPrice())
            .add(request.getQuantity().multiply(request.getPrice()));
        BigDecimal newAvgPrice = totalCost.divide(totalShares, 4, RoundingMode.HALF_UP);
        
        holding.setQuantity(totalShares);
        holding.setAvgBuyPrice(newAvgPrice);
    } else {
        // CREATE new holding
        holding = Holding.builder()
            .portfolio(portfolio)
            .ticker(request.getTicker().toUpperCase())
            .quantity(request.getQuantity())
            .avgBuyPrice(request.getPrice())
            .build();
    }
    
    holdingRepository.save(holding);
    return toResponse(holding);
}
```

---

## 3.6 OrderService.java

**Purpose**: Handles buy/sell orders with validation.

### Functions

| Function | Parameters | Returns | Purpose |
|----------|------------|---------|---------|
| `getAllOrders()` | none | `List<OrderResponse>` | Get all orders |
| `placeOrder(request)` | `OrderRequest` | `OrderResponse` | Execute buy/sell order |

#### `placeOrder(request)` - BUY Flow
```java
public OrderResponse placeOrder(OrderRequest request) {
    Portfolio portfolio = portfolioService.getOrCreatePortfolio();
    String ticker = request.getTicker().toUpperCase();
    BigDecimal quantity = request.getQuantity();
    BigDecimal price = request.getPrice();
    BigDecimal totalAmount = quantity.multiply(price);
    
    if (request.getOrderType() == OrderType.BUY) {
        // 1. Validate balance
        if (portfolio.getCashBalance().compareTo(totalAmount) < 0) {
            throw new InsufficientFundsException(
                "Insufficient funds. Required: " + totalAmount + 
                ", Available: " + portfolio.getCashBalance()
            );
        }
        
        // 2. Deduct from balance
        portfolio.setCashBalance(portfolio.getCashBalance().subtract(totalAmount));
        portfolioRepository.save(portfolio);
        
        // 3. Update or create holding
        Optional<Holding> existing = holdingRepository
            .findByPortfolioIdAndTicker(portfolio.getId(), ticker);
        
        if (existing.isPresent()) {
            Holding h = existing.get();
            BigDecimal newQty = h.getQuantity().add(quantity);
            BigDecimal newAvg = h.getQuantity().multiply(h.getAvgBuyPrice())
                .add(quantity.multiply(price))
                .divide(newQty, 4, RoundingMode.HALF_UP);
            h.setQuantity(newQty);
            h.setAvgBuyPrice(newAvg);
            holdingRepository.save(h);
        } else {
            Holding newHolding = Holding.builder()
                .portfolio(portfolio)
                .ticker(ticker)
                .quantity(quantity)
                .avgBuyPrice(price)
                .build();
            holdingRepository.save(newHolding);
        }
    }
    
    // 4. Create order record
    Order order = Order.builder()
        .portfolio(portfolio)
        .ticker(ticker)
        .orderType(request.getOrderType())
        .status(OrderStatus.COMPLETED)
        .quantity(quantity)
        .price(price)
        .totalAmount(totalAmount)
        .createdAt(LocalDateTime.now())
        .executedAt(LocalDateTime.now())
        .build();
    
    orderRepository.save(order);
    return toResponse(order);
}
```

#### SELL Flow
```java
if (request.getOrderType() == OrderType.SELL) {
    // 1. Check holding exists
    Holding holding = holdingRepository
        .findByPortfolioIdAndTicker(portfolio.getId(), ticker)
        .orElseThrow(() -> new ResourceNotFoundException("No holding for " + ticker));
    
    // 2. Validate quantity
    if (holding.getQuantity().compareTo(quantity) < 0) {
        throw new InsufficientFundsException(
            "Insufficient shares. Have: " + holding.getQuantity() + 
            ", Selling: " + quantity
        );
    }
    
    // 3. Add to balance
    portfolio.setCashBalance(portfolio.getCashBalance().add(totalAmount));
    portfolioRepository.save(portfolio);
    
    // 4. Reduce or delete holding
    BigDecimal remainingQty = holding.getQuantity().subtract(quantity);
    if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) {
        holdingRepository.delete(holding);
    } else {
        holding.setQuantity(remainingQty);
        holdingRepository.save(holding);
    }
}
```

---

## 3.7 PortfolioAnalyticsService.java

**Purpose**: Calculates portfolio summary with P&L.

### Functions

| Function | Parameters | Returns | Purpose |
|----------|------------|---------|---------|
| `getPortfolioSummary()` | none | `PortfolioSummaryResponse` | Calculate total value, P&L |

```java
public PortfolioSummaryResponse getPortfolioSummary() {
    Portfolio portfolio = portfolioService.getOrCreatePortfolio();
    List<Holding> holdings = holdingRepository.findByPortfolioId(portfolio.getId());
    
    BigDecimal totalInvested = BigDecimal.ZERO;
    BigDecimal currentValue = BigDecimal.ZERO;
    
    for (Holding h : holdings) {
        // Invested = quantity × avgBuyPrice
        BigDecimal invested = h.getQuantity().multiply(h.getAvgBuyPrice());
        totalInvested = totalInvested.add(invested);
        
        // Current = quantity × currentPrice
        BigDecimal currentPrice = yahooFinanceService.getPrice(h.getTicker());
        BigDecimal current = h.getQuantity().multiply(currentPrice);
        currentValue = currentValue.add(current);
    }
    
    BigDecimal profitLoss = currentValue.subtract(totalInvested);
    BigDecimal returnPercent = totalInvested.compareTo(BigDecimal.ZERO) > 0
        ? profitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"))
        : BigDecimal.ZERO;
    
    return PortfolioSummaryResponse.builder()
        .cashBalance(portfolio.getCashBalance())
        .totalInvested(totalInvested)
        .currentValue(currentValue)
        .totalProfitLoss(profitLoss)
        .returnPercent(returnPercent)
        .build();
}
```

---

## 3.8 MutualFundService.java

**Purpose**: Fetches mutual fund data from MFAPI (India).

### External API
```java
private static final String MFAPI_BASE = "https://api.mfapi.in/mf";
```

### Functions

| Function | Parameters | Returns | Purpose |
|----------|------------|---------|---------|
| `getTopMutualFunds()` | none | `List<MutualFundResponse>` | Get top funds (12 hour cache) |
| `getMutualFundDetails(code)` | String | `MutualFundResponse` | Get fund details |
| `searchMutualFunds(query)` | String | `List<MutualFundResponse>` | Search funds |

---

## 3.9 PortfolioService.java

**Purpose**: Basic portfolio operations.

### Functions

| Function | Purpose |
|----------|---------|
| `getOrCreatePortfolio()` | Get portfolio by ID=1 or create with $100,000 |
| `updateCashBalance(amount)` | Update cash balance |

---

## 3.10 WatchlistService.java

**Purpose**: CRUD for watchlist.

### Functions

| Function | Purpose |
|----------|---------|
| `getAll()` | Get all watchlist items |
| `add(ticker)` | Add ticker to watchlist |
| `remove(ticker)` | Remove ticker |
| `exists(ticker)` | Check if exists |

---

# SECTION 4: CONTROLLER CLASSES

## 4.1 MarketDataController.java

```java
@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketDataController {
    
    private final MarketDataService marketDataService;
    private final MutualFundService mutualFundService;
    private final NewsService newsService;

    @GetMapping("/stock/{ticker}")
    public ResponseEntity<StockDetailResponse> getStockDetail(@PathVariable String ticker) {
        return ResponseEntity.ok(marketDataService.getStockDetail(ticker));
    }

    @GetMapping("/stock/{ticker}/history")
    public ResponseEntity<StockHistoryResponse> getStockWithHistory(@PathVariable String ticker) {
        return ResponseEntity.ok(marketDataService.getStockWithHistory(ticker));
    }

    @GetMapping("/gainers")
    public ResponseEntity<List<MarketMoverResponse>> getTopGainers() {
        return ResponseEntity.ok(marketDataService.getTopGainers());
    }

    @GetMapping("/losers")
    public ResponseEntity<List<MarketMoverResponse>> getTopLosers() {
        return ResponseEntity.ok(marketDataService.getTopLosers());
    }

    @GetMapping("/trending")
    public ResponseEntity<List<MarketMoverResponse>> getTrendingStocks() {
        return ResponseEntity.ok(marketDataService.getTrendingStocks());
    }

    @GetMapping("/indices")
    public ResponseEntity<List<Map<String, Object>>> getMarketIndices() {
        return ResponseEntity.ok(marketDataService.getMarketIndices());
    }

    @GetMapping("/news")
    public ResponseEntity<List<Map<String, Object>>> getNews(
            @RequestParam(defaultValue = "false") boolean refresh) {
        return ResponseEntity.ok(newsService.getNews(refresh));
    }

    @GetMapping("/news/{ticker}")
    public ResponseEntity<List<Map<String, Object>>> getStockNews(@PathVariable String ticker) {
        return ResponseEntity.ok(newsService.getStockNews(ticker));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StockDetailResponse>> searchStocks(@RequestParam String q) {
        return ResponseEntity.ok(marketDataService.searchStocks(q));
    }

    @GetMapping("/mutualfunds")
    public ResponseEntity<List<MutualFundResponse>> getTopMutualFunds() {
        return ResponseEntity.ok(mutualFundService.getTopMutualFunds());
    }
}
```

---

## 4.2 Other Controllers

**HoldingController**: `/api/holdings` - GET all, GET by ticker, POST create, DELETE

**OrderController**: `/api/orders` - GET all, POST place order

**PortfolioController**: `/api/portfolio/summary` - GET summary

**PriceController**: `/api/prices/{ticker}` - GET price

**WatchlistController**: `/api/watchlist` - GET all, POST add, DELETE remove
