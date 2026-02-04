package com.portfolio.manager.dto.response;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class OrderResponseTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .ticker("AAPL")
                .orderType("BUY")
                .status("COMPLETED")
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("182.50"))
                .totalAmount(new BigDecimal("1825.00"))
                .createdAt(now)
                .executedAt(now)
                .build();

        assertEquals(1L, response.getId());
        assertEquals("AAPL", response.getTicker());
        assertEquals("BUY", response.getOrderType());
        assertEquals("COMPLETED", response.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        OrderResponse response = new OrderResponse();
        assertNull(response.getId());
        assertNull(response.getTicker());
    }

    @Test
    void testSettersAndGetters() {
        OrderResponse response = new OrderResponse();
        response.setId(1L);
        response.setTicker("MSFT");
        response.setOrderType("SELL");
        response.setStatus("PENDING");
        response.setQuantity(new BigDecimal("5"));
        response.setPrice(new BigDecimal("400"));
        response.setTotalAmount(new BigDecimal("2000"));
        response.setCreatedAt(LocalDateTime.now());

        assertEquals(1L, response.getId());
        assertEquals("MSFT", response.getTicker());
        assertEquals("SELL", response.getOrderType());
    }

    @Test
    void testToString() {
        OrderResponse response = OrderResponse.builder().id(1L).ticker("AAPL").build();
        assertNotNull(response.toString());
        assertTrue(response.toString().contains("AAPL"));
    }
}
