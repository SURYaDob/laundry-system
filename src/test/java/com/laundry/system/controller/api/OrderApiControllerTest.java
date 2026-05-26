package com.laundry.system.controller.api;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.dto.OrderPlacementDto;
import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.exception.GlobalExceptionHandler;
import com.laundry.system.service.CustomUserDetailsService;
import com.laundry.system.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderApiController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrders_ShouldReturnJson() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllOrders_WithCustomerRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllOrders_WithoutAuth_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "cust@test.com")
    void placeOrder_ShouldReturn201() throws Exception {
        when(orderService.placeOrder(anyString(), any(OrderPlacementDto.class)))
                .thenReturn(LaundryOrder.builder().id(1L).orderNumber("ORD-001").build());

        String requestBody = """
                {
                    "pickupDate": "2026-06-01T10:00:00",
                    "items": [
                        {"serviceId": 1, "quantity": 2.0}
                    ],
                    "discount": 0.0
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-001"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "cust@test.com")
    void placeOrder_WithInvalidInput_ShouldReturn400() throws Exception {
        String invalidBody = """
                {
                    "pickupDate": "",
                    "items": [],
                    "discount": -5.0
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatus_ShouldReturnOk() throws Exception {
        when(orderService.updateOrderStatus(1L, "WASHING"))
                .thenReturn(LaundryOrder.builder().id(1L).status("WASHING").build());

        mockMvc.perform(patch("/api/orders/1/status")
                        .param("status", "WASHING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WASHING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }
}
