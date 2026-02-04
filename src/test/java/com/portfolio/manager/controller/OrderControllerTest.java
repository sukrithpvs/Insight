package com.portfolio.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.manager.dto.request.CreateOrderRequest;
import com.portfolio.manager.dto.response.OrderResponse;
import com.portfolio.manager.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;

        @Autowired
        private ObjectMapper objectMapper;

        private OrderResponse orderResponse;

        @BeforeEach
        void setUp() {
                orderResponse = OrderResponse.builder()
                                .id(1L)
                                .ticker("AAPL")
                                .orderType("BUY")
                                .status("COMPLETED")
                                .quantity(new BigDecimal("10.0000"))
                                .price(new BigDecimal("182.5000"))
                                .totalAmount(new BigDecimal("1825.0000"))
                                .createdAt(LocalDateTime.now())
                                .executedAt(LocalDateTime.now())
                                .build();
        }

        @Test
        void testGetAllOrders() throws Exception {
                when(orderService.getAllOrders()).thenReturn(Arrays.asList(orderResponse));

                mockMvc.perform(get("/api/orders"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].ticker").value("AAPL"))
                                .andExpect(jsonPath("$[0].orderType").value("BUY"));

                verify(orderService).getAllOrders();
        }

        @Test
        void testGetAllOrders_Empty() throws Exception {
                when(orderService.getAllOrders()).thenReturn(Arrays.asList());

                mockMvc.perform(get("/api/orders"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void testCreateOrder_BuySuccess() throws Exception {
                CreateOrderRequest request = new CreateOrderRequest();
                request.setTicker("AAPL");
                request.setOrderType("BUY");
                request.setQuantity(new BigDecimal("10"));
                request.setPrice(new BigDecimal("182.50"));

                when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderResponse);

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.ticker").value("AAPL"))
                                .andExpect(jsonPath("$.orderType").value("BUY"))
                                .andExpect(jsonPath("$.status").value("COMPLETED"));
        }

        @Test
        void testCreateOrder_SellSuccess() throws Exception {
                CreateOrderRequest request = new CreateOrderRequest();
                request.setTicker("AAPL");
                request.setOrderType("SELL");
                request.setQuantity(new BigDecimal("5"));
                request.setPrice(new BigDecimal("185.00"));

                OrderResponse sellResponse = OrderResponse.builder()
                                .id(2L)
                                .ticker("AAPL")
                                .orderType("SELL")
                                .status("COMPLETED")
                                .quantity(new BigDecimal("5.0000"))
                                .price(new BigDecimal("185.0000"))
                                .totalAmount(new BigDecimal("925.0000"))
                                .build();

                when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(sellResponse);

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.orderType").value("SELL"));
        }

        @Test
        void testGetOrdersByTicker() throws Exception {
                when(orderService.getOrdersByTicker("AAPL")).thenReturn(Arrays.asList(orderResponse));

                mockMvc.perform(get("/api/orders/ticker/AAPL"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].ticker").value("AAPL"));

                verify(orderService).getOrdersByTicker("AAPL");
        }

        @Test
        void testCancelOrder() throws Exception {
                doNothing().when(orderService).cancelOrder(1L);

                mockMvc.perform(post("/api/orders/1/cancel"))
                                .andExpect(status().isOk());

                verify(orderService).cancelOrder(1L);
        }
}
