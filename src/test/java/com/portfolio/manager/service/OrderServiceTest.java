package com.portfolio.manager.service;

import com.portfolio.manager.dto.request.CreateOrderRequest;
import com.portfolio.manager.dto.response.OrderResponse;
import com.portfolio.manager.dto.response.PriceResponse;
import com.portfolio.manager.entity.Holding;
import com.portfolio.manager.entity.Order;
import com.portfolio.manager.entity.Portfolio;
import com.portfolio.manager.exception.BadRequestException;
import com.portfolio.manager.repository.HoldingRepository;
import com.portfolio.manager.repository.OrderRepository;
import com.portfolio.manager.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private YahooFinanceService yahooFinanceService;

    @InjectMocks
    private OrderService orderService;

    private Portfolio portfolio;
    private Order order;
    private Holding holding;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Test Portfolio");
        portfolio.setCashBalance(new BigDecimal("100000.00"));

        order = Order.builder()
                .id(1L)
                .portfolio(portfolio)
                .ticker("AAPL")
                .orderType(Order.OrderType.BUY)
                .status(Order.OrderStatus.COMPLETED)
                .quantity(new BigDecimal("10.0000"))
                .price(new BigDecimal("182.5000"))
                .totalAmount(new BigDecimal("1825.0000"))
                .createdAt(LocalDateTime.now())
                .executedAt(LocalDateTime.now())
                .build();

        holding = new Holding();
        holding.setId(1L);
        holding.setPortfolio(portfolio);
        holding.setTicker("AAPL");
        holding.setQuantity(new BigDecimal("10.0000"));
        holding.setAvgBuyPrice(new BigDecimal("182.5000"));
    }

    @Test
    void testGetAllOrders() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(orderRepository.findByPortfolioOrderByCreatedAtDesc(portfolio)).thenReturn(Arrays.asList(order));

        var result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
    }

    @Test
    void testGetAllOrders_Empty() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(orderRepository.findByPortfolioOrderByCreatedAtDesc(portfolio)).thenReturn(Arrays.asList());

        var result = orderService.getAllOrders();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrdersByTicker() {
        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(orderRepository.findByPortfolioAndTickerOrderByCreatedAtDesc(portfolio, "AAPL"))
                .thenReturn(Arrays.asList(order));

        var result = orderService.getOrdersByTicker("AAPL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
    }

    @Test
    void testCreateOrder_Buy() {
        // CreateOrderRequest uses String for orderType
        CreateOrderRequest request = new CreateOrderRequest();
        request.setTicker("AAPL");
        request.setOrderType("BUY");
        request.setQuantity(new BigDecimal("10"));
        request.setPrice(new BigDecimal("182.50"));

        PriceResponse priceResponse = PriceResponse.builder()
                .ticker("AAPL")
                .price(new BigDecimal("182.50"))
                .build();

        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(yahooFinanceService.getStockPrice("AAPL")).thenReturn(priceResponse);
        when(holdingRepository.findByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(1L);
            return o;
        });
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);
        when(holdingRepository.save(any(Holding.class))).thenReturn(holding);

        OrderResponse result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals("BUY", result.getOrderType());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrder_Buy_InsufficientFunds() {
        portfolio.setCashBalance(new BigDecimal("100.00")); // Less than needed

        CreateOrderRequest request = new CreateOrderRequest();
        request.setTicker("AAPL");
        request.setOrderType("BUY");
        request.setQuantity(new BigDecimal("10"));
        request.setPrice(new BigDecimal("182.50"));

        PriceResponse priceResponse = PriceResponse.builder()
                .ticker("AAPL")
                .price(new BigDecimal("182.50"))
                .build();

        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(yahooFinanceService.getStockPrice("AAPL")).thenReturn(priceResponse);

        assertThrows(BadRequestException.class, () -> orderService.createOrder(request));
    }

    @Test
    void testCreateOrder_Sell() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setTicker("AAPL");
        request.setOrderType("SELL");
        request.setQuantity(new BigDecimal("5"));
        request.setPrice(new BigDecimal("185.00"));

        PriceResponse priceResponse = PriceResponse.builder()
                .ticker("AAPL")
                .price(new BigDecimal("185.00"))
                .build();

        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(yahooFinanceService.getStockPrice("AAPL")).thenReturn(priceResponse);
        when(holdingRepository.findByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(Optional.of(holding));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(1L);
            return o;
        });
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);
        when(holdingRepository.save(any(Holding.class))).thenReturn(holding);

        OrderResponse result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals("SELL", result.getOrderType());
    }

    @Test
    void testCreateOrder_Sell_InsufficientShares() {
        holding.setQuantity(new BigDecimal("3")); // Less than trying to sell

        CreateOrderRequest request = new CreateOrderRequest();
        request.setTicker("AAPL");
        request.setOrderType("SELL");
        request.setQuantity(new BigDecimal("10"));
        request.setPrice(new BigDecimal("185.00"));

        PriceResponse priceResponse = PriceResponse.builder()
                .ticker("AAPL")
                .price(new BigDecimal("185.00"))
                .build();

        when(portfolioService.getPortfolioEntity()).thenReturn(portfolio);
        when(yahooFinanceService.getStockPrice("AAPL")).thenReturn(priceResponse);
        when(holdingRepository.findByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(Optional.of(holding));

        assertThrows(BadRequestException.class, () -> orderService.createOrder(request));
    }

    @Test
    void testCancelOrder() {
        Order pendingOrder = Order.builder()
                .id(1L)
                .status(Order.OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);

        orderService.cancelOrder(1L);

        verify(orderRepository).save(argThat(o -> o.getStatus() == Order.OrderStatus.CANCELLED));
    }

    @Test
    void testCancelOrder_NotPending() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order)); // Already COMPLETED

        assertThrows(BadRequestException.class, () -> orderService.cancelOrder(1L));
    }
}
