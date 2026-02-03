package com.portfolio.manager.repository;

import com.portfolio.manager.entity.Order;
import com.portfolio.manager.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByPortfolioOrderByCreatedAtDesc(Portfolio portfolio);

    List<Order> findByPortfolioAndTickerOrderByCreatedAtDesc(Portfolio portfolio, String ticker);

    List<Order> findByPortfolioAndStatusOrderByCreatedAtDesc(Portfolio portfolio, Order.OrderStatus status);

    List<Order> findByPortfolioAndOrderTypeOrderByCreatedAtDesc(Portfolio portfolio, Order.OrderType orderType);
}
