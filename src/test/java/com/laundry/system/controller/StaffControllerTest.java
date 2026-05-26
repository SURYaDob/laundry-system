package com.laundry.system.controller;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.service.CustomUserDetailsService;
import com.laundry.system.service.StaffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StaffController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StaffService staffService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void listStaff_ShouldReturnStaffView() throws Exception {
        mockMvc.perform(get("/admin/staff"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff/list"))
                .andExpect(model().attributeExists("staffList", "staffForm"));
    }

    @Test
    void listStaff_WithoutAuth_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin/staff"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void listStaff_WithStaffRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/staff"))
                .andExpect(status().isForbidden());
    }
}
