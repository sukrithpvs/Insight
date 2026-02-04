package com.portfolio.manager.dto.response;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MutualFundResponseTest {

    @Test
    void testBuilder() {
        MutualFundResponse response = MutualFundResponse.builder()
                .schemeCode("119551")
                .schemeName("Axis Bluechip Fund")
                .nav(new BigDecimal("45.50"))
                .navDate("2024-01-15")
                .schemeCategory("Equity")
                .schemeType("Open Ended")
                .fundHouse("Axis Mutual Fund")
                .oneYearReturn(new BigDecimal("15.5"))
                .threeYearReturn(new BigDecimal("12.3"))
                .fiveYearReturn(new BigDecimal("10.8"))
                .build();

        assertEquals("119551", response.getSchemeCode());
        assertEquals("Axis Bluechip Fund", response.getSchemeName());
        assertEquals(new BigDecimal("45.50"), response.getNav());
        assertEquals("2024-01-15", response.getNavDate());
        assertEquals("Equity", response.getSchemeCategory());
        assertEquals("Axis Mutual Fund", response.getFundHouse());
    }

    @Test
    void testNoArgsConstructor() {
        MutualFundResponse response = new MutualFundResponse();
        assertNull(response.getSchemeCode());
        assertNull(response.getSchemeName());
    }

    @Test
    void testSettersAndGetters() {
        MutualFundResponse response = new MutualFundResponse();
        response.setSchemeCode("120503");
        response.setSchemeName("SBI Blue Chip Fund");
        response.setNav(new BigDecimal("75.25"));
        response.setNavDate("2024-02-01");
        response.setSchemeCategory("Large Cap");
        response.setSchemeType("Open Ended");
        response.setFundHouse("SBI Mutual Fund");

        assertEquals("120503", response.getSchemeCode());
        assertEquals("SBI Blue Chip Fund", response.getSchemeName());
        assertEquals(new BigDecimal("75.25"), response.getNav());
    }

    @Test
    void testToString() {
        MutualFundResponse response = MutualFundResponse.builder()
                .schemeCode("100")
                .schemeName("Test Fund")
                .build();
        assertNotNull(response.toString());
        assertTrue(response.toString().contains("Test Fund"));
    }
}
