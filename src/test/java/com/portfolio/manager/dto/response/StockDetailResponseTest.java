package com.portfolio.manager.dto.response;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class StockDetailResponseTest {

    @Test
    void testBuilder() {
        StockDetailResponse response = StockDetailResponse.builder()
                .ticker("AAPL")
                .name("Apple Inc.")
                .price(new BigDecimal("182.50"))
                .change(new BigDecimal("2.50"))
                .changePercent(new BigDecimal("1.39"))
                .marketCap(new BigDecimal("2800000000000"))
                .peRatio(new BigDecimal("28.5"))
                .fiftyTwoWeekHigh(new BigDecimal("200.00"))
                .fiftyTwoWeekLow(new BigDecimal("150.00"))
                .volume(50000000L)
                .avgVolume(45000000L)
                .exchange("NASDAQ")
                .sector("Technology")
                .industry("Consumer Electronics")
                .build();

        assertEquals("AAPL", response.getTicker());
        assertEquals("Apple Inc.", response.getName());
        assertEquals(new BigDecimal("182.50"), response.getPrice());
        assertEquals(50000000L, response.getVolume());
        assertEquals("NASDAQ", response.getExchange());
        assertEquals("Technology", response.getSector());
    }

    @Test
    void testNoArgsConstructor() {
        StockDetailResponse response = new StockDetailResponse();
        assertNull(response.getTicker());
        assertNull(response.getName());
    }

    @Test
    void testSettersAndGetters() {
        StockDetailResponse response = new StockDetailResponse();
        response.setTicker("MSFT");
        response.setName("Microsoft Corporation");
        response.setPrice(new BigDecimal("400.00"));
        response.setChange(new BigDecimal("-5.00"));
        response.setChangePercent(new BigDecimal("-1.25"));
        response.setMarketCap(new BigDecimal("3000000000000"));
        response.setPeRatio(new BigDecimal("35.0"));
        response.setFiftyTwoWeekHigh(new BigDecimal("420.00"));
        response.setFiftyTwoWeekLow(new BigDecimal("300.00"));
        response.setVolume(30000000L);
        response.setAvgVolume(25000000L);
        response.setExchange("NASDAQ");
        response.setSector("Technology");
        response.setIndustry("Software");

        assertEquals("MSFT", response.getTicker());
        assertEquals("Microsoft Corporation", response.getName());
        assertEquals(new BigDecimal("400.00"), response.getPrice());
    }

    @Test
    void testNegativeChange() {
        StockDetailResponse response = StockDetailResponse.builder()
                .ticker("TSLA")
                .change(new BigDecimal("-10.00"))
                .changePercent(new BigDecimal("-5.00"))
                .build();

        assertEquals(new BigDecimal("-10.00"), response.getChange());
        assertEquals(new BigDecimal("-5.00"), response.getChangePercent());
    }

    @Test
    void testToString() {
        StockDetailResponse response = StockDetailResponse.builder().ticker("AAPL").build();
        assertNotNull(response.toString());
        assertTrue(response.toString().contains("AAPL"));
    }
}
