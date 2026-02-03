package com.portfolio.manager.controller;

import com.portfolio.manager.dto.request.AddToWatchlistRequest;
import com.portfolio.manager.dto.response.WatchlistItemResponse;
import com.portfolio.manager.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
@Tag(name = "Watchlist", description = "Stock watchlist management")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    @Operation(summary = "Add a stock to watchlist")
    public ResponseEntity<WatchlistItemResponse> addToWatchlist(
            @Valid @RequestBody AddToWatchlistRequest request) {
        WatchlistItemResponse response = watchlistService.addToWatchlist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all watchlist items")
    public ResponseEntity<List<WatchlistItemResponse>> getWatchlist() {
        return ResponseEntity.ok(watchlistService.getWatchlist());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove item from watchlist by ID")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable Long id) {
        watchlistService.removeFromWatchlist(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/ticker/{ticker}")
    @Operation(summary = "Remove item from watchlist by ticker")
    public ResponseEntity<Void> removeByTicker(@PathVariable String ticker) {
        watchlistService.removeByTicker(ticker);
        return ResponseEntity.noContent().build();
    }
}
