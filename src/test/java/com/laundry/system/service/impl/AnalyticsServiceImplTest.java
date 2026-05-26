package com.laundry.system.service.impl;

import com.laundry.system.entity.Service;
import com.laundry.system.repository.CustomerRepository;
import com.laundry.system.repository.LaundryOrderRepository;
import com.laundry.system.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private LaundryOrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ServiceRepository serviceRepository;

    private AnalyticsServiceImpl analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsServiceImpl(orderRepository, customerRepository, serviceRepository);
    }

    @Test
    void getAdminAnalyticsData_ShouldReturnAllRequiredFields() {
        when(orderRepository.count()).thenReturn(100L);
        when(orderRepository.calculateTotalRevenue()).thenReturn(50000.0);
        when(customerRepository.count()).thenReturn(50L);
        when(orderRepository.countByStatus("PENDING")).thenReturn(5L);
        when(orderRepository.countByStatus("WASHING")).thenReturn(3L);
        when(orderRepository.countByStatus("IRONING")).thenReturn(2L);

        Service s1 = Service.builder().id(1L).name("Wash & Fold").build();
        Service s2 = Service.builder().id(2L).name("Dry Cleaning").build();
        when(serviceRepository.findAll()).thenReturn(List.of(s1, s2));

        Map<String, Object> data = analyticsService.getAdminAnalyticsData();

        assertThat(data).containsKeys("totalOrders", "totalRevenue", "totalCustomers", "pendingOrders",
                "revenueLabels", "revenueValues", "serviceLabels", "serviceCounts");

        assertThat(data.get("totalOrders")).isEqualTo(100L);
        assertThat(data.get("totalRevenue")).isEqualTo(50000.0);
        assertThat(data.get("totalCustomers")).isEqualTo(50L);
        assertThat(data.get("pendingOrders")).isEqualTo(10L); // 5 + 3 + 2

        assertThat(data.get("serviceLabels")).asList().contains("Wash & Fold", "Dry Cleaning");
    }

    @Test
    void getAdminAnalyticsData_ShouldProvideDefaultServiceData_WhenNoServicesExist() {
        when(orderRepository.count()).thenReturn(0L);
        when(orderRepository.calculateTotalRevenue()).thenReturn(0.0);
        when(customerRepository.count()).thenReturn(0L);
        when(orderRepository.countByStatus("PENDING")).thenReturn(0L);
        when(orderRepository.countByStatus("WASHING")).thenReturn(0L);
        when(orderRepository.countByStatus("IRONING")).thenReturn(0L);
        when(serviceRepository.findAll()).thenReturn(List.of());

        Map<String, Object> data = analyticsService.getAdminAnalyticsData();

        assertThat(data.get("serviceLabels")).asList().contains("Wash & Fold", "Steam Ironing", "Dry Cleaning");
    }
}
