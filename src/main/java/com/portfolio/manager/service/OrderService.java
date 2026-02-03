package com.portfolio.manager.service;

import com.portfolio.manager.dto.request.CreateOrderRequest;
import com.portfolio.manager.dto.response.OrderResponse;
import com.portfolio.manager.entity.Holding;
import com.portfolio.manager.entity.Order;
import com.portfolio.manager.entity.Portfolio;
import com.portfolio.manager.exception.BadRequestException;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.repository.HoldingRepository;
import com.portfolio.manager.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final HoldingRepository holdingRepository;
    private final PortfolioService portfolioService;
    private final YahooFinanceService yahooFinanceService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        String ticker = request.getTicker().toUpperCase();
        Order.OrderType orderType = Order.OrderType.valueOf(request.getOrderType().toUpperCase());

        log.info("Creating {} order for {} - {} shares @ ${}",
                orderType, ticker, request.getQuantity(), request.getPrice());

        Portfolio portfolio = portfolioService.getPortfolioEntity();

        // For SELL orders, verify user has enough shares
        if (orderType == Order.OrderType.SELL) {
            Optional<Holding> holding = holdingRepository.findByPortfolioAndTicker(portfolio, ticker);
            if (holding.isEmpty()) {
                throw new BadRequestException("You don't own any shares of " + ticker);
            }
            if (holding.get().getQuantity().compareTo(request.getQuantity()) < 0) {
                throw new BadRequestException("Insufficient shares. You only have " +
                        holding.get().getQuantity() + " shares of " + ticker);
            }
        }

        // Create and save the order
        Order order = Order.builder()
                .portfolio(portfolio)
                .ticker(ticker)
                .orderType(orderType)
                .status(Order.OrderStatus.COMPLETED) // Instant execution for demo
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .totalAmount(request.getQuantity().multiply(request.getPrice()))
                .executedAt(LocalDateTime.now())
                .build();

        Order saved = orderRepository.save(order);

        // Update holdings based on order type
        updateHoldings(portfolio, ticker, request.getQuantity(), request.getPrice(), orderType);

        log.info("Order {} completed: {} {} shares of {} @ ${}",
                saved.getId(), orderType, request.getQuantity(), ticker, request.getPrice());

        return toResponse(saved);
    }

    private void updateHoldings(Portfolio portfolio, String ticker, BigDecimal quantity,
            BigDecimal price, Order.OrderType orderType) {
        Optional<Holding> existingHolding = holdingRepository.findByPortfolioAndTicker(portfolio, ticker);

        if (orderType == Order.OrderType.BUY) {
            if (existingHolding.isPresent()) {
                // Update existing holding with weighted average price
                Holding holding = existingHolding.get();
                BigDecimal totalShares = holding.getQuantity().add(quantity);
                BigDecimal totalValue = holding.getQuantity().multiply(holding.getAvgBuyPrice())
                        .add(quantity.multiply(price));
                BigDecimal newAvgPrice = totalValue.divide(totalShares, 4, java.math.RoundingMode.HALF_UP);

                holding.setQuantity(totalShares);
                holding.setAvgBuyPrice(newAvgPrice);
                holdingRepository.save(holding);
            } else {
                // Create new holding
                Holding newHolding = Holding.builder()
                        .portfolio(portfolio)
                        .ticker(ticker)
                        .quantity(quantity)
                        .avgBuyPrice(price)
                        .build();
                holdingRepository.save(newHolding);
            }
        } else { // SELL
            Holding holding = existingHolding.orElseThrow();
            BigDecimal remainingShares = holding.getQuantity().subtract(quantity);

            if (remainingShares.compareTo(BigDecimal.ZERO) <= 0) {
                // Remove holding entirely
                holdingRepository.delete(holding);
            } else {
                holding.setQuantity(remainingShares);
                holdingRepository.save(holding);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        Portfolio portfolio = portfolioService.getPortfolioEntity();
        return orderRepository.findByPortfolioOrderByCreatedAtDesc(portfolio).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByTicker(String ticker) {
        Portfolio portfolio = portfolioService.getPortfolioEntity();
        return orderRepository.findByPortfolioAndTickerOrderByCreatedAtDesc(portfolio, ticker.toUpperCase())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be cancelled");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .ticker(order.getTicker())
                .orderType(order.getOrderType().name())
                .status(order.getStatus().name())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .executedAt(order.getExecutedAt())
                .build();
    }
}
