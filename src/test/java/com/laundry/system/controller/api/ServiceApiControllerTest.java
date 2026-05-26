package com.laundry.system.controller.api;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.entity.Service;
import com.laundry.system.service.CustomUserDetailsService;
import com.laundry.system.service.ServiceCrudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceApiController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class ServiceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceCrudService serviceCrudService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getAllServices_ShouldReturnJson() throws Exception {
        when(serviceCrudService.getAllServices()).thenReturn(List.of(
                Service.builder().id(1L).name("Wash").basePrice(50.0).pricePerKg(30.0).build()
        ));

        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Wash"));
    }

    @Test
    void getServiceById_ShouldReturnService() throws Exception {
        when(serviceCrudService.getServiceById(1L))
                .thenReturn(Service.builder().id(1L).name("Dry Clean").build());

        mockMvc.perform(get("/api/services/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dry Clean"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createService_ShouldReturn201() throws Exception {
        when(serviceCrudService.createService(any(Service.class)))
                .thenReturn(Service.builder().id(1L).name("Premium Wash").basePrice(100.0).pricePerKg(40.0).build());

        String requestBody = """
                {
                    "name": "Premium Wash",
                    "description": "Premium wash service",
                    "basePrice": 100.0,
                    "pricePerKg": 40.0
                }
                """;

        mockMvc.perform(post("/api/services")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Premium Wash"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteService_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/services/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getServices_WithoutAuth_ShouldStillWork() throws Exception {
        // Public endpoint - should work without auth
        when(serviceCrudService.getAllServices()).thenReturn(List.of());
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk());
    }
}
