package com.laundry.system.controller;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.entity.Customer;
import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.entity.User;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private StaffRepository staffRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void listAllOrders_ShouldReturnOrdersView() throws Exception {
        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attributeExists("orders", "staffList"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewOrderDetails_ShouldReturnDetailsView() throws Exception {
        User user = User.builder().id(1L).firstName("John").lastName("Doe").build();
        Customer customer = Customer.builder().id(1L).user(user).build();
        LaundryOrder order = LaundryOrder.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .customer(customer)
                .build();
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/admin/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/details"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewOrderDetails_ShouldRedirect_WhenNotFound() throws Exception {
        when(orderService.getOrderById(anyLong())).thenThrow(new RuntimeException("not found"));

        mockMvc.perform(get("/admin/orders/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders?error=Order not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignStaff_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/admin/orders/assign")
                        .with(csrf())
                        .param("orderId", "1")
                        .param("staffId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders?success=Staff+member+assigned+successfully%21"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatus_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/admin/orders/status")
                        .with(csrf())
                        .param("orderId", "1")
                        .param("status", "WASHING"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders?success=Order+status+updated+to+WASHING%21"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/admin/orders/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders?success=Order+deleted+successfully%21"));
    }

    @Test
    void listAllOrders_WithoutAuth_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void listAllOrders_WithWrongRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isForbidden());
    }
}
