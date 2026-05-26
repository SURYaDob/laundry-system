package com.laundry.system.controller;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.service.AnalyticsService;
import com.laundry.system.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportsController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewAnalytics_ShouldReturnAnalyticsView() throws Exception {
        when(analyticsService.getAdminAnalyticsData()).thenReturn(Map.of(
                "totalOrders", 100L,
                "totalRevenue", 50000.0,
                "totalCustomers", 50L
        ));

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/analytics"))
                .andExpect(model().attributeExists("totalOrders", "totalRevenue", "totalCustomers"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void viewAnalytics_WithWrongRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isForbidden());
    }
}
