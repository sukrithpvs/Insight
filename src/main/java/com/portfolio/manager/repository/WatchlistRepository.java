package com.portfolio.manager.repository;

import com.portfolio.manager.entity.Portfolio;
import com.portfolio.manager.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {

    List<WatchlistItem> findByPortfolioOrderByAddedAtDesc(Portfolio portfolio);

    Optional<WatchlistItem> findByPortfolioAndTicker(Portfolio portfolio, String ticker);

    boolean existsByPortfolioAndTicker(Portfolio portfolio, String ticker);
}
