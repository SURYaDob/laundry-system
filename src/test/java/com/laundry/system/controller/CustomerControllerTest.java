package com.laundry.system.controller;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.dto.OrderPlacementDto;
import com.laundry.system.service.CustomUserDetailsService;
import com.laundry.system.service.OrderService;
import com.laundry.system.repository.CustomerRepository;
import com.laundry.system.repository.LaundryOrderRepository;
import com.laundry.system.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ServiceRepository serviceRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private LaundryOrderRepository orderRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "customer@test.com")
    void listMyOrders_ShouldReturnCustomerListView() throws Exception {
        when(customerRepository.findByUserEmail("customer@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/customer/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/customer-list"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void showOrderPlacementForm_ShouldReturnNewOrderView() throws Exception {
        when(serviceRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/customer/orders/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/new"))
                .andExpect(model().attributeExists("orderForm", "catalog"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "customer@test.com")
    void placeLaundryOrder_ShouldRedirect_OnSuccess() throws Exception {
        mockMvc.perform(post("/customer/orders/new")
                        .with(csrf())
                        .param("pickupDate", "2026-06-01T10:00:00")
                        .param("items[0].serviceId", "1")
                        .param("items[0].quantity", "2.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/orders?success=Laundry+pickup+scheduled+successfully%21"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "customer@test.com")
    void placeLaundryOrder_ShouldReturnForm_OnValidationError() throws Exception {
        mockMvc.perform(post("/customer/orders/new")
                        .with(csrf())
                        .param("pickupDate", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/new"))
                .andExpect(model().attributeExists("catalog"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "customer@test.com")
    void placeLaundryOrder_ShouldReturnForm_OnServiceError() throws Exception {
        when(orderService.placeOrder(anyString(), any(OrderPlacementDto.class)))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/customer/orders/new")
                        .with(csrf())
                        .param("pickupDate", "2026-06-01T10:00:00")
                        .param("items[0].serviceId", "1")
                        .param("items[0].quantity", "2.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/new"))
                .andExpect(model().attributeExists("error", "catalog"));
    }

    @Test
    void listMyOrders_WithoutAuth_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/customer/orders"))
                .andExpect(status().is3xxRedirection());
    }
}
