package com.laundry.system.controller;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.entity.Customer;
import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.entity.Staff;
import com.laundry.system.entity.User;
import com.laundry.system.repository.LaundryOrderRepository;
import com.laundry.system.repository.StaffRepository;
import com.laundry.system.service.CustomUserDetailsService;
import com.laundry.system.service.OrderService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StaffOperationsController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class StaffOperationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private StaffRepository staffRepository;

    @MockBean
    private LaundryOrderRepository orderRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "STAFF", username = "staff@test.com")
    void listAssignedOrders_ShouldReturnStaffView() throws Exception {
        User user = User.builder().id(2L).firstName("Staff").lastName("User").build();
        Staff staff = Staff.builder().id(1L).user(user).build();
        when(staffRepository.findByUserEmail("staff@test.com")).thenReturn(Optional.of(staff));
        when(orderRepository.findByStaffIdOrderByCreatedDateDesc(1L)).thenReturn(List.of());

        mockMvc.perform(get("/staff/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/staff"))
                .andExpect(model().attributeExists("assignedOrders"));
    }

    @Test
    @WithMockUser(roles = "STAFF", username = "staff@test.com")
    void viewAssignedOrderDetails_ShouldReturnDetails_WhenAssigned() throws Exception {
        User user = User.builder().id(2L).firstName("Staff").lastName("User").build();
        Customer customer = Customer.builder()
                .id(1L)
                .user(User.builder().id(3L).firstName("Jane").lastName("Smith").build())
                .build();
        Staff staff = Staff.builder().id(1L).user(user).build();
        LaundryOrder order = LaundryOrder.builder()
                .id(1L)
                .staff(staff)
                .customer(customer)
                .build();

        when(staffRepository.findByUserEmail("staff@test.com")).thenReturn(Optional.of(staff));
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/staff/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/staff-details"));
    }

    @Test
    @WithMockUser(roles = "STAFF", username = "staff@test.com")
    void viewAssignedOrderDetails_ShouldRedirectToAccessDenied_WhenNotAssigned() throws Exception {
        Staff staff = Staff.builder().id(1L).build();
        Staff otherStaff = Staff.builder().id(2L).build();
        LaundryOrder order = LaundryOrder.builder()
                .id(1L)
                .staff(otherStaff)
                .build();

        when(staffRepository.findByUserEmail("staff@test.com")).thenReturn(Optional.of(staff));
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/staff/orders/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(roles = "STAFF", username = "staff@test.com")
    void updateOrderProgress_ShouldRedirect() throws Exception {
        User user = User.builder().id(2L).firstName("Staff").lastName("User").build();
        Customer customer = Customer.builder()
                .id(1L)
                .user(User.builder().id(3L).firstName("Jane").lastName("Smith").build())
                .build();
        Staff staff = Staff.builder().id(1L).user(user).build();
        LaundryOrder order = LaundryOrder.builder()
                .id(1L)
                .staff(staff)
                .customer(customer)
                .build();

        when(staffRepository.findByUserEmail("staff@test.com")).thenReturn(Optional.of(staff));
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(post("/staff/orders/status")
                        .with(csrf())
                        .param("orderId", "1")
                        .param("status", "WASHING"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/orders/1?success=Order+status+advanced+to+WASHING%21"));
    }
}
