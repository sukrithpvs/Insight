package com.portfolio.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MutualFundResponse {

    private String schemeCode;
    private String schemeName;
    private String fundHouse;
    private String schemeType;
    private String schemeCategory;
    private BigDecimal nav;
    private String navDate;
    private BigDecimal oneYearReturn;
    private BigDecimal threeYearReturn;
    private BigDecimal fiveYearReturn;
}
