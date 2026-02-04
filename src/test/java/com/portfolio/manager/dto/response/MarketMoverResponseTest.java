package com.portfolio.manager.dto.response;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MarketMoverResponseTest {

    @Test
    void testBuilder() {
        MarketMoverResponse response = MarketMoverResponse.builder()
                .ticker("AAPL")
                .name("Apple Inc.")
                .price(new BigDecimal("182.50"))
                .change(new BigDecimal("5.50"))
                .changePercent(new BigDecimal("3.10"))
                .volume(50000000L)
                .marketCap(new BigDecimal("2800000000000"))
                .build();

        assertEquals("AAPL", response.getTicker());
        assertEquals("Apple Inc.", response.getName());
        assertEquals(new BigDecimal("182.50"), response.getPrice());
        assertEquals(new BigDecimal("5.50"), response.getChange());
        assertEquals(new BigDecimal("3.10"), response.getChangePercent());
        assertEquals(50000000L, response.getVolume());
    }

    @Test
    void testNoArgsConstructor() {
        MarketMoverResponse response = new MarketMoverResponse();
        assertNull(response.getTicker());
        assertNull(response.getPrice());
    }

    @Test
    void testSettersAndGetters() {
        MarketMoverResponse response = new MarketMoverResponse();
        response.setTicker("NVDA");
        response.setName("NVIDIA Corporation");
        response.setPrice(new BigDecimal("500.00"));
        response.setChange(new BigDecimal("-10.00"));
        response.setChangePercent(new BigDecimal("-2.00"));
        response.setVolume(30000000L);

        assertEquals("NVDA", response.getTicker());
        assertEquals("NVIDIA Corporation", response.getName());
        assertEquals(new BigDecimal("500.00"), response.getPrice());
        assertEquals(new BigDecimal("-10.00"), response.getChange());
    }

    @Test
    void testNegativeChange() {
        MarketMoverResponse response = MarketMoverResponse.builder()
                .ticker("META")
                .change(new BigDecimal("-15.00"))
                .changePercent(new BigDecimal("-3.50"))
                .build();

        assertTrue(response.getChange().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(response.getChangePercent().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void testToString() {
        MarketMoverResponse response = MarketMoverResponse.builder().ticker("AMZN").build();
        assertNotNull(response.toString());
        assertTrue(response.toString().contains("AMZN"));
    }
}
