package com.portfolio.manager.repository;

import com.portfolio.manager.entity.MarketCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketCacheRepository extends JpaRepository<MarketCache, Long> {
    Optional<MarketCache> findByCacheKey(String cacheKey);
}
