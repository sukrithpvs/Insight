package com.portfolio.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PortfolioManagerApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context loads successfully
    }

    @Test
    void mainMethodTest() {
        // Test main method
        PortfolioManagerApplication.main(new String[] {});
    }
}
