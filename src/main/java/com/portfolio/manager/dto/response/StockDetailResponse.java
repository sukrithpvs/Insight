package com.portfolio.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDetailResponse {

    private String ticker;
    private String name;
    private String exchange;
    private String currency;

    // Price Data
    private BigDecimal price;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal previousClose;
    private Long volume;
    private Long avgVolume;

    // Change
    private BigDecimal change;
    private BigDecimal changePercent;

    // Fundamentals
    private BigDecimal marketCap;
    private BigDecimal peRatio;
    private BigDecimal eps;
    private BigDecimal dividend;
    private BigDecimal dividendYield;
    private BigDecimal fiftyTwoWeekHigh;
    private BigDecimal fiftyTwoWeekLow;
    private BigDecimal beta;

    // Additional Info
    private String sector;
    private String industry;
    private String description;

    private String timestamp;
}
