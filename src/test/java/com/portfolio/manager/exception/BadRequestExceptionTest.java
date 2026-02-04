package com.portfolio.manager.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Insufficient funds. Required: $1825.00, Available: $100.00";
        BadRequestException exception = new BadRequestException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionThrow() {
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException("Test exception");
        });
    }

    @Test
    void testResourceNotFoundExceptionMessage() {
        String message = "Stock with ticker INVALID not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testResourceNotFoundExceptionThrow() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Resource not found");
        });
    }
}
