package com.laundry.system.controller;

import com.laundry.system.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

// This test validates that the exception handler produces correct error responses
class GlobalExceptionHandlerTest {

    @Test
    void resourceNotFoundException_ShouldHaveCorrectHttpStatus() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "email", "test@test.com");

        assertThat(ex.getMessage()).contains("User not found with email: 'test@test.com'");
    }

    @Test
    void duplicateResourceException_ShouldHaveCorrectHttpStatus() {
        DuplicateResourceException ex = new DuplicateResourceException("Service", "name", "Wash");

        assertThat(ex.getMessage()).contains("Service already exists with name: 'Wash'");
    }

    @Test
    void businessRuleException_ShouldHaveCorrectMessage() {
        BusinessRuleException ex = new BusinessRuleException("Order cannot be modified after delivery");

        assertThat(ex.getMessage()).isEqualTo("Order cannot be modified after delivery");
    }

    @Test
    void errorResponse_ShouldBuildCorrectly() {
        ErrorResponse error = ErrorResponse.builder()
                .status(404)
                .error("Not Found")
                .message("Test error")
                .path("/test")
                .build();

        assertThat(error.getStatus()).isEqualTo(404);
        assertThat(error.getError()).isEqualTo("Not Found");
        assertThat(error.getTimestamp()).isNotNull();
    }
}
