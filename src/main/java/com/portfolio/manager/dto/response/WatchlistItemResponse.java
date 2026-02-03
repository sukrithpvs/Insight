package com.portfolio.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistItemResponse {

    private Long id;
    private String ticker;
    private String companyName;
    private BigDecimal currentPrice;
    private BigDecimal changePercent;
    private LocalDateTime addedAt;
    private String notes;
}
