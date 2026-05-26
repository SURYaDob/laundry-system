package com.laundry.system.controller;

import com.laundry.system.config.TestSecurityConfig;
import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.service.CustomUserDetailsService;
import com.laundry.system.service.InvoiceService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void downloadInvoice_ShouldReturnPdf() throws Exception {
        LaundryOrder order = LaundryOrder.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .build();

        byte[] pdfBytes = "%PDF-1.4 test pdf content".getBytes();

        when(orderService.getOrderById(1L)).thenReturn(order);
        when(invoiceService.generateInvoicePdf(1L)).thenReturn(pdfBytes);

        mockMvc.perform(get("/customer/invoices/download/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", containsString("invoice-ORD-001.pdf")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void downloadInvoice_ShouldReturn404_WhenOrderNotFound() throws Exception {
        when(invoiceService.generateInvoicePdf(anyLong())).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/customer/invoices/download/999"))
                .andExpect(status().isNotFound());
    }
}
