package com.laundry.system.service.impl;

import com.laundry.system.entity.*;
import com.laundry.system.exception.ResourceNotFoundException;
import com.laundry.system.repository.LaundryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private LaundryOrderRepository orderRepository;

    private InvoiceServiceImpl invoiceService;

    @BeforeEach
    void setUp() {
        invoiceService = new InvoiceServiceImpl(orderRepository);
    }

    @Test
    void generateInvoicePdf_ShouldGenerateValidPdf_WhenOrderExists() {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("INV-001")
                .issuedDate(LocalDateTime.now())
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .user(User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email("john@test.com")
                        .build())
                .phoneNumber("1234567890")
                .address("123 Test St")
                .build();

        com.laundry.system.entity.Service service = com.laundry.system.entity.Service.builder()
                .id(1L)
                .name("Wash & Fold")
                .basePrice(50.0)
                .pricePerKg(30.0)
                .build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .service(service)
                .quantity(2.0)
                .unitPrice(30.0)
                .subTotal(110.0)
                .build();

        LaundryOrder order = LaundryOrder.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .customer(customer)
                .status("DELIVERED")
                .totalAmount(110.0)
                .tax(19.8)
                .discount(0.0)
                .finalAmount(129.8)
                .invoice(invoice)
                .orderItems(List.of(item))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        byte[] pdfBytes = invoiceService.generateInvoicePdf(1L);

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(100); // PDF should have content
        // Verify PDF header
        assertThat(pdfBytes[0]).isEqualTo((byte) 0x25); // '%'
        assertThat(pdfBytes[1]).isEqualTo((byte) 0x50); // 'P'
        assertThat(pdfBytes[2]).isEqualTo((byte) 0x44); // 'D'
        assertThat(pdfBytes[3]).isEqualTo((byte) 0x46); // 'F'
    }

    @Test
    void generateInvoicePdf_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.generateInvoicePdf(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with id: '99'");
    }
}
