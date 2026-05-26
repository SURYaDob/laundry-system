package com.laundry.system.repository;

import com.laundry.system.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LaundryOrderRepositoryTest {

    @Autowired
    private LaundryOrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    private Customer testCustomer;
    private com.laundry.system.entity.Service testService;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build());
        User user = userRepository.save(User.builder()
                .email("customer@test.com")
                .password("password")
                .firstName("Test")
                .lastName("Customer")
                .active(true)
                .roles(Set.of(role))
                .build());

        testCustomer = customerRepository.save(Customer.builder()
                .user(user)
                .phoneNumber("1234567890")
                .address("123 Test St")
                .build());

        testService = serviceRepository.save(com.laundry.system.entity.Service.builder()
                .name("Wash & Fold")
                .description("Standard wash and fold service")
                .basePrice(50.0)
                .pricePerKg(30.0)
                .build());
    }

    @Test
    void findByOrderNumber_ShouldReturnOrder_WhenExists() {
        LaundryOrder order = createTestOrder("ORD-TEST-001");

        Optional<LaundryOrder> found = orderRepository.findByOrderNumber("ORD-TEST-001");
        assertThat(found).isPresent();
        assertThat(found.get().getOrderNumber()).isEqualTo("ORD-TEST-001");
        assertThat(found.get().getStatus()).isEqualTo("PENDING");
    }

    @Test
    void findByCustomerId_ShouldReturnOrders() {
        createTestOrder("ORD-TEST-002");
        createTestOrder("ORD-TEST-003");

        List<LaundryOrder> orders = orderRepository.findByCustomerIdOrderByCreatedDateDesc(testCustomer.getId());
        assertThat(orders).hasSize(2);
    }

    @Test
    void countByStatus_ShouldReturnCorrectCount() {
        createTestOrder("ORD-TEST-004");
        createTestOrder("ORD-TEST-005");

        long count = orderRepository.countByStatus("PENDING");
        assertThat(count).isEqualTo(2);
    }

    @Test
    void calculateTotalRevenue_ShouldReturnSumOfNonCancelledOrders() {
        createTestOrder("ORD-TEST-006", "DELIVERED", 500.0, 90.0, 0.0, 590.0);
        createTestOrder("ORD-TEST-007", "CANCELLED", 200.0, 36.0, 0.0, 236.0);

        Double revenue = orderRepository.calculateTotalRevenue();
        assertThat(revenue).isGreaterThan(0);
    }

    private LaundryOrder createTestOrder(String orderNumber) {
        return createTestOrder(orderNumber, "PENDING", 100.0, 18.0, 0.0, 118.0);
    }

    private LaundryOrder createTestOrder(String orderNumber, String status,
                                         Double total, Double tax, Double discount, Double finalAmount) {
        return orderRepository.save(LaundryOrder.builder()
                .orderNumber(orderNumber)
                .customer(testCustomer)
                .status(status)
                .totalAmount(total)
                .tax(tax)
                .discount(discount)
                .finalAmount(finalAmount)
                .build());
    }
}
