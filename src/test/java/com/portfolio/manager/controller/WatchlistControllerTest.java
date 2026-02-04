package com.portfolio.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.manager.dto.request.AddToWatchlistRequest;
import com.portfolio.manager.dto.response.WatchlistItemResponse;
import com.portfolio.manager.service.WatchlistService;
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

@WebMvcTest(WatchlistController.class)
class WatchlistControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private WatchlistService watchlistService;

        @Autowired
        private ObjectMapper objectMapper;

        private WatchlistItemResponse watchlistResponse;

        @BeforeEach
        void setUp() {
                watchlistResponse = WatchlistItemResponse.builder()
                                .id(1L)
                                .ticker("NVDA")
                                .companyName("NVIDIA Corp.")
                                .currentPrice(new BigDecimal("850.00"))
                                .changePercent(new BigDecimal("2.50"))
                                .addedAt(LocalDateTime.now())
                                .notes("Watch for dip")
                                .build();
        }

        @Test
        void testGetWatchlist() throws Exception {
                when(watchlistService.getWatchlist()).thenReturn(Arrays.asList(watchlistResponse));

                mockMvc.perform(get("/api/watchlist"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].ticker").value("NVDA"))
                                .andExpect(jsonPath("$[0].companyName").value("NVIDIA Corp."));

                verify(watchlistService).getWatchlist();
        }

        @Test
        void testGetWatchlist_Empty() throws Exception {
                when(watchlistService.getWatchlist()).thenReturn(Arrays.asList());

                mockMvc.perform(get("/api/watchlist"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void testAddToWatchlist() throws Exception {
                AddToWatchlistRequest request = new AddToWatchlistRequest();
                request.setTicker("AMD");
                request.setNotes("Good for long term");

                WatchlistItemResponse addedResponse = WatchlistItemResponse.builder()
                                .id(2L)
                                .ticker("AMD")
                                .companyName("AMD Inc.")
                                .currentPrice(new BigDecimal("180.00"))
                                .notes("Good for long term")
                                .build();

                when(watchlistService.addToWatchlist(any(AddToWatchlistRequest.class))).thenReturn(addedResponse);

                mockMvc.perform(post("/api/watchlist")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.ticker").value("AMD"));
        }

        @Test
        void testRemoveFromWatchlist() throws Exception {
                doNothing().when(watchlistService).removeFromWatchlist(1L);

                mockMvc.perform(delete("/api/watchlist/1"))
                                .andExpect(status().isNoContent());

                verify(watchlistService).removeFromWatchlist(1L);
        }

        @Test
        void testRemoveByTicker() throws Exception {
                doNothing().when(watchlistService).removeByTicker("NVDA");

                mockMvc.perform(delete("/api/watchlist/ticker/NVDA"))
                                .andExpect(status().isNoContent());

                verify(watchlistService).removeByTicker("NVDA");
        }
}
