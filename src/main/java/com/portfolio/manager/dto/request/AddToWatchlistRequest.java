package com.portfolio.manager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToWatchlistRequest {

    @NotBlank(message = "Ticker symbol is required")
    private String ticker;

    private String notes;
}
