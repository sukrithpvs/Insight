package com.portfolio.manager.controller;

import com.portfolio.manager.dto.response.PortfolioSummaryResponse;
import com.portfolio.manager.service.PortfolioAnalyticsService;
import com.portfolio.manager.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private PortfolioService portfolioService;

        @MockBean
        private PortfolioAnalyticsService analyticsService;

        private PortfolioSummaryResponse summaryResponse;

        @BeforeEach
        void setUp() {
                // Use correct field name: profitLoss (not totalProfitLoss)
                summaryResponse = PortfolioSummaryResponse.builder()
                                .cashBalance(new BigDecimal("98175.00"))
                                .totalInvested(new BigDecimal("4205.50"))
                                .currentValue(new BigDecimal("4350.25"))
                                .profitLoss(new BigDecimal("144.75"))
                                .returnPercent(new BigDecimal("3.44"))
                                .build();
        }

        @Test
        void testGetPortfolioSummary() throws Exception {
                when(analyticsService.getPortfolioSummary()).thenReturn(summaryResponse);

                mockMvc.perform(get("/api/portfolio/summary"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.cashBalance").value(98175.00))
                                .andExpect(jsonPath("$.totalInvested").value(4205.50))
                                .andExpect(jsonPath("$.currentValue").value(4350.25))
                                .andExpect(jsonPath("$.profitLoss").value(144.75))
                                .andExpect(jsonPath("$.returnPercent").value(3.44));

                verify(analyticsService).getPortfolioSummary();
        }

        @Test
        void testGetPortfolioSummary_ZeroHoldings() throws Exception {
                PortfolioSummaryResponse emptyResponse = PortfolioSummaryResponse.builder()
                                .cashBalance(new BigDecimal("100000.00"))
                                .totalInvested(BigDecimal.ZERO)
                                .currentValue(BigDecimal.ZERO)
                                .profitLoss(BigDecimal.ZERO)
                                .returnPercent(BigDecimal.ZERO)
                                .build();

                when(analyticsService.getPortfolioSummary()).thenReturn(emptyResponse);

                mockMvc.perform(get("/api/portfolio/summary"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.cashBalance").value(100000.00))
                                .andExpect(jsonPath("$.totalInvested").value(0));
        }

        @Test
        void testGetPortfolioSummary_NegativeReturn() throws Exception {
                PortfolioSummaryResponse lossResponse = PortfolioSummaryResponse.builder()
                                .cashBalance(new BigDecimal("95000.00"))
                                .totalInvested(new BigDecimal("5000.00"))
                                .currentValue(new BigDecimal("4500.00"))
                                .profitLoss(new BigDecimal("-500.00"))
                                .returnPercent(new BigDecimal("-10.00"))
                                .build();

                when(analyticsService.getPortfolioSummary()).thenReturn(lossResponse);

                mockMvc.perform(get("/api/portfolio/summary"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.profitLoss").value(-500.00))
                                .andExpect(jsonPath("$.returnPercent").value(-10.00));
        }
}
