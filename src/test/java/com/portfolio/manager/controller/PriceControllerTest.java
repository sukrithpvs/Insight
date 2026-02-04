package com.portfolio.manager.controller;

import com.portfolio.manager.dto.response.PriceResponse;
import com.portfolio.manager.service.YahooFinanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private YahooFinanceService yahooFinanceService;

    private PriceResponse priceResponse;

    @BeforeEach
    void setUp() {
        // PriceResponse has: ticker, price, currency, timestamp
        priceResponse = PriceResponse.builder()
                .ticker("AAPL")
                .price(new BigDecimal("182.50"))
                .currency("USD")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetPrice() throws Exception {
        when(yahooFinanceService.getStockPrice("AAPL")).thenReturn(priceResponse);

        mockMvc.perform(get("/api/prices/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("AAPL"))
                .andExpect(jsonPath("$.price").value(182.50))
                .andExpect(jsonPath("$.currency").value("USD"));

        verify(yahooFinanceService).getStockPrice("AAPL");
    }

    @Test
    void testGetPrice_Lowercase() throws Exception {
        when(yahooFinanceService.getStockPrice("aapl")).thenReturn(priceResponse);

        mockMvc.perform(get("/api/prices/aapl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("AAPL"));
    }

    @Test
    void testGetPrice_DifferentStock() throws Exception {
        PriceResponse tslaResponse = PriceResponse.builder()
                .ticker("TSLA")
                .price(new BigDecimal("248.75"))
                .currency("USD")
                .build();

        when(yahooFinanceService.getStockPrice("TSLA")).thenReturn(tslaResponse);

        mockMvc.perform(get("/api/prices/TSLA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("TSLA"))
                .andExpect(jsonPath("$.price").value(248.75));
    }

    @Test
    void testGetPrice_Null() throws Exception {
        when(yahooFinanceService.getStockPrice("INVALID")).thenReturn(null);

        mockMvc.perform(get("/api/prices/INVALID"))
                .andExpect(status().isOk());
    }
}
