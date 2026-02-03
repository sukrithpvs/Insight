package com.portfolio.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity to store cached market data (gainers, losers, indices) in database.
 * Cache is refreshed every hour by scheduled task.
 */
@Entity
@Table(name = "market_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cache_key", nullable = false, unique = true)
    private String cacheKey;

    @Column(name = "cache_value", columnDefinition = "TEXT")
    private String cacheValue; // JSON string of cached data

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired(long ttlMinutes) {
        if (updatedAt == null)
            return true;
        return LocalDateTime.now().isAfter(updatedAt.plusMinutes(ttlMinutes));
    }
}
