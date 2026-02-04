# 🌐 API Endpoints Documentation

## Overview

| Controller | Base Path | Total Endpoints |
|------------|-----------|-----------------|
| MarketDataController | `/api/market` | 12 |
| HoldingController | `/api/holdings` | 4 |
| OrderController | `/api/orders` | 2 |
| PortfolioController | `/api/portfolio` | 1 |
| PriceController | `/api/prices` | 1 |
| WatchlistController | `/api/watchlist` | 3 |

---

# MARKET DATA ENDPOINTS (`/api/market`)

## 1. GET `/api/market/gainers`

**Purpose**: Get top 5 gaining stocks

**Flow:**
```
Frontend                    Backend                         Database/API
─────────                  ───────                         ────────────
MarketPulse.jsx  ──GET──►  MarketDataController            
                               │                           
                               ▼                           
                          MarketDataService                
                          .getTopGainers()                 
                               │                           
                               ▼                           
                          getCachedOrFetch("top_gainers")  
                               │                           
            ┌──────────────────┴──────────────────┐
            ▼                                      ▼
      [CACHE HIT]                           [CACHE MISS]
      Return JSON from DB                   Fetch from Yahoo Finance
            │                                      │
            │                               For each ticker:
            │                               YahooFinance.get(ticker)
            │                                      │
            │                               Save to market_cache
            │                                      │
            └──────────────┬───────────────────────┘
                           ▼
                    Return Response
```

**SQL Executed:**
```sql
-- Check cache
SELECT mc1_0.id, mc1_0.cache_key, mc1_0.cache_value, mc1_0.updated_at 
FROM market_cache mc1_0 
WHERE mc1_0.cache_key = 'top_gainers';
```

**Response:**
```json
[
  {
    "ticker": "NVDA",
    "name": "NVIDIA Corp.",
    "price": 682.35,
    "change": 28.42,
    "changePercent": 4.35,
    "volume": 45000000,
    "marketCap": 1680000000000
  }
]
```

---

## 2. GET `/api/market/losers`

**Purpose**: Get top 5 losing stocks

**Flow**: Same as `/gainers` but with cache key `top_losers` and filters for negative change.

---

## 3. GET `/api/market/trending`

**Purpose**: Get 8 trending stocks

**Flow**: Same pattern, cache key `trending_stocks`.

---

## 4. GET `/api/market/indices`

**Purpose**: Get market indices (S&P 500, NASDAQ, DOW)

**Response:**
```json
[
  { "name": "S&P 500", "value": 5021.84, "change": 12.45, "changePercent": 0.25 },
  { "name": "NASDAQ", "value": 15990.66, "change": -28.50, "changePercent": -0.18 },
  { "name": "DOW", "value": 38519.84, "change": 85.20, "changePercent": 0.22 }
]
```

---

## 5. GET `/api/market/stock/{ticker}`

**Purpose**: Get detailed stock information

**Flow:**
```
StockDetailPage.jsx  ──GET /api/market/stock/AAPL──►  MarketDataController
                                                            │
                                                            ▼
                                                   MarketDataService
                                                   .getStockDetail("AAPL")
                                                            │
                              ┌─────────────────────────────┼─────────────────────────────┐
                              ▼                             ▼                             ▼
                        [TRY Yahoo]                   [TRY Finnhub]                  [Use Mock]
                        YahooFinance.get("AAPL")      finnhubService                 buildMockDetail()
                              │                       .getStockQuote("AAPL")              │
                              │                             │                             │
                              └─────────────────────────────┴─────────────────────────────┘
                                                            │
                                                            ▼
                                                     Return Response
```

**External API Called (Yahoo):**
```
YahooFinance.get("AAPL") → Yahoo API internally
```

**External API Called (Finnhub - if Yahoo fails):**
```
GET https://finnhub.io/api/v1/quote?symbol=AAPL&token=ct10...
```

**Response:**
```json
{
  "ticker": "AAPL",
  "name": "Apple Inc.",
  "exchange": "NASDAQ",
  "currency": "USD",
  "price": 182.50,
  "open": 181.20,
  "high": 183.50,
  "low": 180.90,
  "previousClose": 179.65,
  "volume": 52340000,
  "avgVolume": 48500000,
  "change": 2.85,
  "changePercent": 1.59,
  "marketCap": 2850000000000,
  "peRatio": 28.50,
  "eps": 6.40,
  "fiftyTwoWeekHigh": 199.62,
  "fiftyTwoWeekLow": 164.08
}
```

---

## 6. GET `/api/market/stock/{ticker}/history`

**Purpose**: Get stock with 5-year historical data for charts

**Flow:**
```
StockDetailPage.jsx  ──GET /api/market/stock/AAPL/history──►  Controller
                                                                  │
                                                                  ▼
                                                         MarketDataService
                                                         .getStockWithHistory("AAPL")
                                                                  │
                                             ┌────────────────────┴────────────────────┐
                                             ▼                                         ▼
                                       [Check Cache]                             [Cache Miss]
                                       stock_history_AAPL                        Fetch from Yahoo
                                             │                                   5 years of data
                                             │                                         │
                                             │                                   Save to cache
                                             │                                         │
                                             └─────────────────┬───────────────────────┘
                                                               ▼
                                                        Return Response
```

**SQL:**
```sql
SELECT mc1_0.id, mc1_0.cache_key, mc1_0.cache_value, mc1_0.updated_at 
FROM market_cache mc1_0 
WHERE mc1_0.cache_key = 'stock_history_AAPL';
```

**Response:**
```json
{
  "ticker": "AAPL",
  "name": "Apple Inc.",
  "price": 182.50,
  "historicalData": [
    { "date": "2021-02-04", "open": 134.50, "high": 135.20, "low": 133.80, "close": 134.99, "volume": 45000000 },
    { "date": "2021-02-05", "open": 135.00, "high": 136.10, "low": 134.50, "close": 135.85, "volume": 42000000 }
    // ... 5 years of daily data
  ]
}
```

---

## 7. GET `/api/market/news`

**Purpose**: Get general market news

**Flow:**
```
NewsSection.jsx  ──GET /api/market/news──►  MarketDataController
                                                   │
                                                   ▼
                                            NewsService.getNews()
                                                   │
                              ┌────────────────────┴────────────────────┐
                              ▼                                         ▼
                        [Cache Hit]                               [Cache Miss]
                        market_news                               Fetch from Yahoo RSS
                              │                                   ──────────────────
                              │                                   GET https://finance.yahoo.com
                              │                                       /rss/topstories
                              │                                         │
                              │                                   Parse XML → JSON
                              │                                   Save to cache
                              │                                         │
                              └─────────────────┬───────────────────────┘
                                                ▼
                                         Return Response
```

**External API:**
```
GET https://finance.yahoo.com/rss/topstories
Returns: RSS XML
```

**Response:**
```json
[
  {
    "id": 1,
    "title": "Fed Signals Potential Rate Cut in March",
    "description": "The Federal Reserve indicated...",
    "link": "https://finance.yahoo.com/news/fed-rate-cut",
    "source": "Yahoo Finance",
    "time": "2h ago"
  }
]
```

---

## 8. GET `/api/market/news/{ticker}`

**Purpose**: Get stock-specific news

**Flow:**
```
StockDetailPage.jsx  ──GET /api/market/news/AAPL──►  Controller
                                                          │
                                                          ▼
                                                   NewsService
                                                   .getStockNews("AAPL")
                                                          │
                              ┌───────────────────────────┼───────────────────────────┐
                              ▼                           ▼                           ▼
                        [Cache Hit]                 [Yahoo RSS]                 [Finnhub Fallback]
                        stock_news_AAPL             feeds.finance.yahoo.com     finnhub.io/api/v1
                              │                     /rss/2.0/headline?s=AAPL    /company-news
                              │                           │                           │
                              │                           │                     ┌─────┴─────┐
                              │                           │                     ▼           ▼
                              │                           │               [Finnhub]    [Mock News]
                              │                           │                     │           │
                              └───────────────────────────┴─────────────────────┴───────────┘
                                                          │
                                                    Save to cache
                                                          │
                                                   Return Response
```

**External APIs:**
```
1. Yahoo RSS: https://feeds.finance.yahoo.com/rss/2.0/headline?s=AAPL
2. Finnhub: https://finnhub.io/api/v1/company-news?symbol=AAPL&from=2026-01-28&to=2026-02-04&token=KEY
```

---

## 9. GET `/api/market/search?q={query}`

**Purpose**: Search stocks by ticker or name

**Flow:**
```
SearchBar.jsx  ──GET /api/market/search?q=NVDA──►  Controller
                                                        │
                                                        ▼
                                                 MarketDataService
                                                 .searchStocks("NVDA")
                                                        │
                               ┌────────────────────────┴────────────────────────┐
                               ▼                                                  ▼
                         [Direct Ticker]                                  [Match Known Stocks]
                         getStockDetail("NVDA")                           Filter US_STOCKS list
                               │                                                  │
                               └────────────────────────┬─────────────────────────┘
                                                        ▼
                                                 Return up to 10 results
```

**Response:**
```json
[
  {
    "ticker": "NVDA",
    "name": "NVIDIA Corp.",
    "price": 682.35,
    "changePercent": 4.35
  }
]
```

---

## 10-12. Mutual Fund Endpoints

| Endpoint | Purpose | External API |
|----------|---------|--------------|
| GET `/api/market/mutualfunds` | Get top funds | `https://api.mfapi.in/mf` |
| GET `/api/market/mutualfunds/{code}` | Get fund details | `https://api.mfapi.in/mf/{code}` |
| GET `/api/market/mutualfunds/search?q=` | Search funds | Filter cached list |

---

# HOLDINGS ENDPOINTS (`/api/holdings`)

## 1. GET `/api/holdings`

**Flow:**
```
InvestmentsRibbon.jsx  ──GET /api/holdings──►  HoldingController
                                                      │
                                                      ▼
                                              HoldingService.getAllHoldings()
                                                      │
                                                      ▼
                                              holdingRepository.findAll()
                                                      │
                                                      ▼
                                              ┌───────────────────────────────┐
                                              │ SELECT h.id, h.ticker,        │
                                              │        h.quantity,            │
                                              │        h.avg_buy_price,       │
                                              │        h.created_at,          │
                                              │        h.portfolio_id         │
                                              │ FROM holdings h               │
                                              └───────────────────────────────┘
                                                      │
                                                      ▼
                                               Return Response
```

**Response:**
```json
[
  {
    "id": 1,
    "ticker": "AAPL",
    "quantity": 10.0000,
    "avgBuyPrice": 182.5000,
    "investedAmount": 1825.0000,
    "createdAt": "2026-02-04T10:30:00"
  }
]
```

---

## 2. POST `/api/holdings`

**Purpose**: Create or update holding

**Request:**
```json
{
  "ticker": "NVDA",
  "quantity": 5,
  "avgBuyPrice": 682.35
}
```

**Flow:**
```
Request  ──POST──►  HoldingController
                          │
                          ▼
                   HoldingService.createOrUpdateHolding()
                          │
           ┌──────────────┴──────────────┐
           ▼                              ▼
    [Ticker Exists?]              [New Holding]
    Yes: Update quantity          INSERT INTO holdings
    & recalculate avg price       (ticker, qty, price...)
           │                              │
           ▼                              │
    UPDATE holdings               ◄───────┘
    SET quantity=?, avg=?
           │
           ▼
    Return updated holding
```

---

## 3. DELETE `/api/holdings/{ticker}`

**SQL:**
```sql
DELETE FROM holdings 
WHERE ticker = 'AAPL' AND portfolio_id = 1;
```

---

# ORDERS ENDPOINTS (`/api/orders`)

## 1. GET `/api/orders`

**SQL:**
```sql
SELECT o.id, o.ticker, o.order_type, o.status, o.quantity, 
       o.price, o.total_amount, o.created_at, o.executed_at 
FROM orders o 
ORDER BY o.created_at DESC;
```

---

## 2. POST `/api/orders`

**Purpose**: Place buy/sell order

**Request:**
```json
{
  "ticker": "AAPL",
  "orderType": "BUY",
  "quantity": 10,
  "price": 182.50
}
```

**Flow (BUY):**
```
Request  ──POST──►  OrderController
                          │
                          ▼
                   OrderService.placeOrder()
                          │
                          ▼
┌──────────────────────────────────────────────────────────────────────┐
│ 1. GET portfolio                                                      │
│    SELECT * FROM portfolios WHERE id = 1                             │
│                                                                       │
│ 2. Calculate total = 10 × 182.50 = 1825                              │
│                                                                       │
│ 3. Check balance: 100000 >= 1825? ✓                                  │
│                                                                       │
│ 4. UPDATE portfolios SET cash_balance = 98175 WHERE id = 1           │
│                                                                       │
│ 5. Check existing holding                                             │
│    SELECT * FROM holdings WHERE ticker = 'AAPL'                      │
│                                                                       │
│ 6a. If exists: UPDATE holdings SET quantity = newQty, avg = newAvg   │
│ 6b. If new: INSERT INTO holdings (ticker, qty, avg...)               │
│                                                                       │
│ 7. INSERT INTO orders (ticker, type, status, qty, price, total...)   │
└──────────────────────────────────────────────────────────────────────┘
                          │
                          ▼
                   Return OrderResponse
```

**Flow (SELL):**
```
1. Check holding exists and qty >= sell qty
2. UPDATE portfolios SET cash_balance = cash_balance + (qty × price)
3. UPDATE holdings SET quantity = quantity - sell_qty (or DELETE if 0)
4. INSERT INTO orders (...)
```

---

# PORTFOLIO ENDPOINTS (`/api/portfolio`)

## GET `/api/portfolio/summary`

**Flow:**
```
Dashboard.jsx  ──GET /api/portfolio/summary──►  PortfolioController
                                                       │
                                                       ▼
                                              PortfolioAnalyticsService
                                              .getPortfolioSummary()
                                                       │
              ┌────────────────────────────────────────┴────────────────────┐
              ▼                                                              ▼
       Get Portfolio                                                  Get all Holdings
       SELECT * FROM portfolios                                       SELECT * FROM holdings
              │                                                              │
              │                                        For each holding:     │
              │                                        Get current price     │
              │                                        from Yahoo Finance    │
              │                                              │               │
              └───────────────────────┬──────────────────────┴───────────────┘
                                      ▼
                               Calculate:
                               • totalInvested = Σ(qty × avgPrice)
                               • currentValue = Σ(qty × currentPrice)
                               • profitLoss = currentValue - totalInvested
                               • returnPercent = (profitLoss / totalInvested) × 100
                                      │
                                      ▼
                               Return Response
```

**Response:**
```json
{
  "cashBalance": 98175.00,
  "totalInvested": 4205.50,
  "currentValue": 4350.25,
  "totalProfitLoss": 144.75,
  "returnPercent": 3.44
}
```

---

# PRICE ENDPOINTS (`/api/prices`)

## GET `/api/prices/{ticker}`

**Flow:**
```
Frontend  ──GET /api/prices/AAPL──►  PriceController
                                           │
                                           ▼
                                    YahooFinanceService
                                    .getStockPrice("AAPL")
                                           │
                     ┌─────────────────────┴─────────────────────┐
                     ▼                                           ▼
              [5-min Cache Hit]                           [Cache Miss]
              Return cached price                         Try Yahoo → Finnhub → Mock
                     │                                           │
                     │                                    Cache result
                     │                                           │
                     └─────────────────┬─────────────────────────┘
                                       ▼
                                Return Response
```

---

# WATCHLIST ENDPOINTS (`/api/watchlist`)

| Method | Endpoint | SQL |
|--------|----------|-----|
| GET | `/api/watchlist` | `SELECT * FROM watchlist_items WHERE portfolio_id = 1` |
| POST | `/api/watchlist` | `INSERT INTO watchlist_items (ticker, company_name, ...)` |
| DELETE | `/api/watchlist/{ticker}` | `DELETE FROM watchlist_items WHERE ticker = ?` |
