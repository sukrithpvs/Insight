package com.portfolio.manager.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);

        order = new Order();
        order.setId(1L);
        order.setPortfolio(portfolio);
        order.setTicker("AAPL");
        order.setOrderType(Order.OrderType.BUY);
        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setQuantity(new BigDecimal("10.0000"));
        order.setPrice(new BigDecimal("182.5000"));
        order.setTotalAmount(new BigDecimal("1825.0000"));
        order.setCreatedAt(LocalDateTime.now());
        order.setExecutedAt(LocalDateTime.now());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, order.getId());
        assertEquals("AAPL", order.getTicker());
        assertEquals(Order.OrderType.BUY, order.getOrderType());
        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
        assertEquals(new BigDecimal("10.0000"), order.getQuantity());
        assertEquals(new BigDecimal("182.5000"), order.getPrice());
        assertEquals(new BigDecimal("1825.0000"), order.getTotalAmount());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getExecutedAt());
    }

    @Test
    void testOrderType_BUY() {
        order.setOrderType(Order.OrderType.BUY);
        assertEquals(Order.OrderType.BUY, order.getOrderType());
    }

    @Test
    void testOrderType_SELL() {
        order.setOrderType(Order.OrderType.SELL);
        assertEquals(Order.OrderType.SELL, order.getOrderType());
    }

    @Test
    void testOrderStatus_PENDING() {
        order.setStatus(Order.OrderStatus.PENDING);
        assertEquals(Order.OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void testOrderStatus_COMPLETED() {
        order.setStatus(Order.OrderStatus.COMPLETED);
        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void testOrderStatus_CANCELLED() {
        order.setStatus(Order.OrderStatus.CANCELLED);
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void testBuilder() {
        Order builtOrder = Order.builder()
                .id(2L)
                .ticker("MSFT")
                .orderType(Order.OrderType.SELL)
                .status(Order.OrderStatus.PENDING)
                .quantity(new BigDecimal("5.0000"))
                .price(new BigDecimal("378.9000"))
                .totalAmount(new BigDecimal("1894.5000"))
                .build();

        assertEquals(2L, builtOrder.getId());
        assertEquals("MSFT", builtOrder.getTicker());
        assertEquals(Order.OrderType.SELL, builtOrder.getOrderType());
        assertEquals(Order.OrderStatus.PENDING, builtOrder.getStatus());
    }

    @Test
    void testOnCreate() {
        Order newOrder = new Order();
        newOrder.setTicker("tsla");
        newOrder.onCreate();

        // onCreate uppercases ticker and sets createdAt
        assertEquals("TSLA", newOrder.getTicker());
        assertNotNull(newOrder.getCreatedAt());
        // Note: onCreate does NOT set status - that's done by the service
    }

    @Test
    void testOnCreate_TickerNull() {
        Order newOrder = new Order();
        newOrder.onCreate();

        assertNull(newOrder.getTicker());
        assertNotNull(newOrder.getCreatedAt());
    }
}
