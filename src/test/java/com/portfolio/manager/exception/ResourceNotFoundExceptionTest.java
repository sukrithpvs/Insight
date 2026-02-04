package com.portfolio.manager.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Portfolio not found");

        assertEquals("Portfolio not found", exception.getMessage());
    }

    @Test
    void testConstructorWithResourceNameAndFieldValue() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Portfolio", "id", 1L);

        assertEquals("Portfolio not found with id: '1'", exception.getMessage());
    }

    @Test
    void testConstructorWithStringValue() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Stock", "ticker", "AAPL");

        assertEquals("Stock not found with ticker: 'AAPL'", exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}
