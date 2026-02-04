package com.portfolio.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.manager.dto.request.AddHoldingRequest;
import com.portfolio.manager.dto.response.HoldingResponse;
import com.portfolio.manager.service.HoldingService;
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

@WebMvcTest(HoldingController.class)
class HoldingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HoldingService holdingService;

    @Autowired
    private ObjectMapper objectMapper;

    private HoldingResponse holdingResponse;

    @BeforeEach
    void setUp() {
        holdingResponse = HoldingResponse.builder()
                .id(1L)
                .ticker("AAPL")
                .quantity(new BigDecimal("10.0000"))
                .avgBuyPrice(new BigDecimal("182.5000"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetAllHoldings() throws Exception {
        when(holdingService.getAllHoldings()).thenReturn(Arrays.asList(holdingResponse));

        mockMvc.perform(get("/api/holdings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticker").value("AAPL"))
                .andExpect(jsonPath("$[0].quantity").value(10.0));

        verify(holdingService).getAllHoldings();
    }

    @Test
    void testGetAllHoldings_Empty() throws Exception {
        when(holdingService.getAllHoldings()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/holdings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testAddHolding() throws Exception {
        AddHoldingRequest request = new AddHoldingRequest();
        request.setTicker("AAPL");
        request.setQuantity(new BigDecimal("10"));
        request.setAvgBuyPrice(new BigDecimal("182.50"));

        when(holdingService.addHolding(any(AddHoldingRequest.class))).thenReturn(holdingResponse);

        mockMvc.perform(post("/api/holdings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticker").value("AAPL"));

        verify(holdingService).addHolding(any(AddHoldingRequest.class));
    }

    @Test
    void testDeleteHolding() throws Exception {
        doNothing().when(holdingService).deleteHolding(1L);

        mockMvc.perform(delete("/api/holdings/1"))
                .andExpect(status().isNoContent());

        verify(holdingService).deleteHolding(1L);
    }
}
