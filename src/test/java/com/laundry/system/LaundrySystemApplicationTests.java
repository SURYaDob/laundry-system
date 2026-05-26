package com.laundry.system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LaundrySystemApplicationTests {

    @Test
    void contextLoads() {
        // Smoke test: verifies the application context starts successfully
    }
}
