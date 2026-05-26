package com.laundry.system.controller;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.service.CustomUserDetailsService;
import com.laundry.system.service.ServiceCrudService;
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

@WebMvcTest(ServiceController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceCrudService serviceCrudService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void listServices_ShouldReturnServicesView() throws Exception {
        mockMvc.perform(get("/admin/services"))
                .andExpect(status().isOk())
                .andExpect(view().name("services/list"))
                .andExpect(model().attributeExists("services", "serviceForm"));
    }

    @Test
    void listServices_WithoutAuth_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin/services"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void listServices_WithWrongRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/services"))
                .andExpect(status().isForbidden());
    }
}
