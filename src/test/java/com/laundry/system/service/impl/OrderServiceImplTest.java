package com.laundry.system.service.impl;

import com.laundry.system.dto.OrderItemDto;
import com.laundry.system.dto.OrderPlacementDto;
import com.laundry.system.entity.*;
import com.laundry.system.exception.ResourceNotFoundException;
import com.laundry.system.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private LaundryOrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private NotificationRepository notificationRepository;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, customerRepository, serviceRepository,
                staffRepository, paymentRepository, invoiceRepository, notificationRepository);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        LaundryOrder order1 = LaundryOrder.builder().id(1L).orderNumber("ORD-001").build();
        LaundryOrder order2 = LaundryOrder.builder().id(2L).orderNumber("ORD-002").build();
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<LaundryOrder> result = orderService.getAllOrders();

        assertThat(result).hasSize(2);
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenExists() {
        LaundryOrder order = LaundryOrder.builder().id(1L).orderNumber("ORD-001").build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        LaundryOrder result = orderService.getOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-001");
    }

    @Test
    void getOrderById_ShouldThrowException_WhenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with id: '99'");
    }

    @Test
    void getOrderByOrderNumber_ShouldReturnOrder_WhenExists() {
        LaundryOrder order = LaundryOrder.builder().orderNumber("ORD-001").build();
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));

        LaundryOrder result = orderService.getOrderByOrderNumber("ORD-001");

        assertThat(result).isNotNull();
    }

    @Test
    void updateOrderStatus_ShouldUpdateAndNotify() {
        Customer customer = Customer.builder()
                .id(1L)
                .user(User.builder().id(1L).email("cust@test.com").build())
                .build();

        LaundryOrder order = LaundryOrder.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .customer(customer)
                .status("PENDING")
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(LaundryOrder.class))).thenReturn(order);

        LaundryOrder result = orderService.updateOrderStatus(1L, "WASHING");

        assertThat(result.getStatus()).isEqualTo("WASHING");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void updateOrderStatus_ShouldCompletePayment_WhenDelivered() {
        Customer customer = Customer.builder()
                .id(1L)
                .user(User.builder().id(1L).email("cust@test.com").build())
                .build();

        Payment payment = Payment.builder().status("PENDING").build();

        LaundryOrder order = LaundryOrder.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .customer(customer)
                .status("OUT_FOR_DELIVERY")
                .payment(payment)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(LaundryOrder.class))).thenReturn(order);

        LaundryOrder result = orderService.updateOrderStatus(1L, "DELIVERED");

        assertThat(result.getPayment().getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void assignStaff_ShouldAssignAndNotifyBothParties() {
        Customer customer = Customer.builder()
                .id(1L)
                .user(User.builder().id(1L).email("cust@test.com").build())
                .build();

        User staffUser = User.builder().id(2L).firstName("John").lastName("Staff").build();
        Staff staff = Staff.builder().id(1L).user(staffUser).build();

        LaundryOrder order = LaundryOrder.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .customer(customer)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
        when(orderRepository.save(any(LaundryOrder.class))).thenReturn(order);

        LaundryOrder result = orderService.assignStaff(1L, 1L);

        assertThat(result.getStaff()).isEqualTo(staff);
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void placeOrder_ShouldThrowException_WhenCustomerNotFound() {
        when(customerRepository.findByUserEmail("unknown@test.com")).thenReturn(Optional.empty());

        OrderPlacementDto dto = OrderPlacementDto.builder()
                .pickupDate("2026-06-01T10:00:00")
                .items(List.of(OrderItemDto.builder().serviceId(1L).quantity(2.0).build()))
                .build();

        assertThatThrownBy(() -> orderService.placeOrder("unknown@test.com", dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer");
    }

    @Test
    void deleteOrder_ShouldDelete_WhenExists() {
        LaundryOrder order = LaundryOrder.builder().id(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository).delete(order);
    }
}
