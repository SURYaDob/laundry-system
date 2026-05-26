package com.laundry.system.config;

import com.laundry.system.service.CustomUserDetailsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test configuration that provides mock beans needed by SecurityConfig.
 * This is imported alongside SecurityConfig in @WebMvcTest tests.
 */
@TestConfiguration
@Import(SecurityConfig.class)
public class TestSecurityConfig {

    // This config imports the real SecurityConfig.
    // CustomUserDetailsService must be @MockBean'd in each test class.
}
