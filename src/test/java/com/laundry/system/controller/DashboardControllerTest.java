package com.laundry.system.controller;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.entity.Staff;
import com.laundry.system.entity.User;
import com.laundry.system.repository.*;
import com.laundry.system.service.CustomUserDetailsService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private StaffRepository staffRepository;

    @MockBean
    private LaundryOrderRepository laundryOrderRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void dashboard_WithoutAuth_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@test.com")
    void dashboard_WithAdminRole_ShouldRedirectToAdminDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "STAFF", username = "staff@test.com")
    void dashboard_WithStaffRole_ShouldRedirectToStaffDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "cust@test.com")
    void dashboard_WithCustomerRole_ShouldRedirectToCustomerDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@test.com")
    void adminDashboard_ShouldReturnAdminView() throws Exception {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(
                User.builder().id(1L).email("admin@test.com").build()));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/admin"))
                .andExpect(model().attributeExists("totalOrders", "totalRevenue", "pendingDeliveries", "activeCustomers"));
    }

    @Test
    @WithMockUser(roles = "STAFF", username = "staff@test.com")
    void staffDashboard_ShouldReturnStaffView() throws Exception {
        User staffUser = User.builder().id(2L).firstName("Staff").lastName("User").build();
        when(userRepository.findByEmail("staff@test.com")).thenReturn(Optional.of(staffUser));
        when(staffRepository.findByUserEmail("staff@test.com")).thenReturn(Optional.of(
                Staff.builder().id(1L).user(staffUser).build()));
        when(laundryOrderRepository.findByStaffIdOrderByCreatedDateDesc(1L)).thenReturn(List.of());

        mockMvc.perform(get("/staff/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/staff"))
                .andExpect(model().attributeExists("assignedOrders"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "cust@test.com")
    void customerDashboard_ShouldReturnCustomerView() throws Exception {
        User customerUser = User.builder().id(3L).firstName("Cust").lastName("User").build();
        com.laundry.system.entity.Customer customer = com.laundry.system.entity.Customer.builder()
                .id(1L)
                .user(customerUser)
                .phoneNumber("1234567890")
                .address("123 Test St")
                .build();
        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customerUser));
        when(customerRepository.findByUserEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(laundryOrderRepository.findByCustomerIdOrderByCreatedDateDesc(1L)).thenReturn(List.of());

        mockMvc.perform(get("/customer/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/customer"))
                .andExpect(model().attributeExists("myOrders"));
    }
}
