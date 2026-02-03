package com.portfolio.manager.controller;

import com.portfolio.manager.dto.request.CreateOrderRequest;
import com.portfolio.manager.dto.response.OrderResponse;
import com.portfolio.manager.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Buy/Sell order management")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new buy/sell order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/ticker/{ticker}")
    @Operation(summary = "Get orders for a specific stock")
    public ResponseEntity<List<OrderResponse>> getOrdersByTicker(@PathVariable String ticker) {
        return ResponseEntity.ok(orderService.getOrdersByTicker(ticker));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a pending order")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
}
