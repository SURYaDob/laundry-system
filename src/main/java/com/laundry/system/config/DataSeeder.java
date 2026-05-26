package com.laundry.system.config;

import com.laundry.system.entity.*;
import com.laundry.system.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final ServiceRepository serviceRepository;
    private final LaundryOrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository,
                      CustomerRepository customerRepository, StaffRepository staffRepository,
                      ServiceRepository serviceRepository, LaundryOrderRepository orderRepository,
                      PaymentRepository paymentRepository,
                      InvoiceRepository invoiceRepository, NotificationRepository notificationRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.serviceRepository = serviceRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("Database already contains data — skipping seeder.");
            return;
        }

        log.info("===== SEEDING DEMO DATA =====");

        try {
            // ──────────────────────────────────────────────
            // 1. ROLES
            // ──────────────────────────────────────────────
            Role adminRole = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
            Role staffRole = roleRepository.save(Role.builder().name("ROLE_STAFF").build());
            Role customerRole = roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build());
            log.info("Created roles: ADMIN, STAFF, CUSTOMER");

            // ──────────────────────────────────────────────
            // 2. SERVICES
            // ──────────────────────────────────────────────
            com.laundry.system.entity.Service washService = serviceRepository.save(
                    com.laundry.system.entity.Service.builder()
                            .name("Standard Wash")
                            .description("Complete washing service with eco-friendly detergents. Suitable for daily wear cotton, linens, and mixed fabrics.")
                            .pricePerKg(80.0)
                            .basePrice(120.0)
                            .build());

            com.laundry.system.entity.Service dryCleanService = serviceRepository.save(
                    com.laundry.system.entity.Service.builder()
                            .name("Dry Clean")
                            .description("Professional dry cleaning for suits, blazers, silk sarees, and delicate fabrics. Stain treatment included.")
                            .pricePerKg(150.0)
                            .basePrice(200.0)
                            .build());

            com.laundry.system.entity.Service ironService = serviceRepository.save(
                    com.laundry.system.entity.Service.builder()
                            .name("Ironing Only")
                            .description("Professional steam ironing and finishing. Perfect for formals, shirts, and trousers.")
                            .pricePerKg(40.0)
                            .basePrice(60.0)
                            .build());

            com.laundry.system.entity.Service washFoldService = serviceRepository.save(
                    com.laundry.system.entity.Service.builder()
                            .name("Wash & Fold")
                            .description("Wash, dry, and neatly folded. Ideal for everyday laundry with quick turnaround.")
                            .pricePerKg(100.0)
                            .basePrice(150.0)
                            .build());

            com.laundry.system.entity.Service premiumService = serviceRepository.save(
                    com.laundry.system.entity.Service.builder()
                            .name("Premium Care")
                            .description("Hand wash, steaming, and gentle care for luxury fabrics including silk, wool, cashmere, and designer wear.")
                            .pricePerKg(200.0)
                            .basePrice(300.0)
                            .build());

            log.info("Created 5 laundry services");

            // ──────────────────────────────────────────────
            // 3. DEMO USERS
            // ──────────────────────────────────────────────
            String defaultPassword = passwordEncoder.encode("password123");

            // ── Admin User ──
            userRepository.save(User.builder()
                    .email("admin@aquaclean.com")
                    .password(defaultPassword)
                    .firstName("Arjun")
                    .lastName("Sharma")
                    .active(true)
                    .roles(Set.of(adminRole, staffRole))
                    .build());

            // ── Staff 1: Raj ──
            User rajUser = userRepository.save(User.builder()
                    .email("raj@aquaclean.com")
                    .password(defaultPassword)
                    .firstName("Raj")
                    .lastName("Verma")
                    .active(true)
                    .roles(Set.of(staffRole))
                    .build());

            staffRepository.save(Staff.builder()
                    .user(rajUser)
                    .phoneNumber("9876543211")
                    .specialization("Washing & Dry Cleaning")
                    .status("ACTIVE")
                    .build());

            // ── Staff 2: Priya ──
            User priyaUser = userRepository.save(User.builder()
                    .email("priya@aquaclean.com")
                    .password(defaultPassword)
                    .firstName("Priya")
                    .lastName("Patel")
                    .active(true)
                    .roles(Set.of(staffRole))
                    .build());

            staffRepository.save(Staff.builder()
                    .user(priyaUser)
                    .phoneNumber("9876543212")
                    .specialization("Ironing & Folding")
                    .status("ACTIVE")
                    .build());

            // ── Customer 1: Amit ──
            User amitUser = userRepository.save(User.builder()
                    .email("amit@example.com")
                    .password(defaultPassword)
                    .firstName("Amit")
                    .lastName("Kumar")
                    .active(true)
                    .roles(Set.of(customerRole))
                    .build());

            Customer amitCustomer = customerRepository.save(Customer.builder()
                    .user(amitUser)
                    .phoneNumber("9876543210")
                    .address("42, Sunshine Apartments, MG Road, Mumbai - 400001")
                    .build());

            // ── Customer 2: Neha ──
            User nehaUser = userRepository.save(User.builder()
                    .email("neha@example.com")
                    .password(defaultPassword)
                    .firstName("Neha")
                    .lastName("Singh")
                    .active(true)
                    .roles(Set.of(customerRole))
                    .build());

            Customer nehaCustomer = customerRepository.save(Customer.builder()
                    .user(nehaUser)
                    .phoneNumber("8765432109")
                    .address("15, Green Valley Colony, Koramangala, Bangalore - 560034")
                    .build());

            log.info("Created demo users:");
            log.info("  ADMIN   -> admin@aquaclean.com / password123");
            log.info("  STAFF   -> raj@aquaclean.com / password123  (Washing & Dry Cleaning)");
            log.info("  STAFF   -> priya@aquaclean.com / password123 (Ironing & Folding)");
            log.info("  CUSTOMER -> amit@example.com / password123");
            log.info("  CUSTOMER -> neha@example.com / password123");

            // ──────────────────────────────────────────────
            // 4. DEMO ORDERS
            // ──────────────────────────────────────────────
            LocalDateTime now = LocalDateTime.now();
            Staff rajStaff = staffRepository.findByUserEmail("raj@aquaclean.com").orElseThrow();
            Staff priyaStaff = staffRepository.findByUserEmail("priya@aquaclean.com").orElseThrow();

            // ── Order 1: Amit — Washing (DELIVERED) ──
            createOrder(amitCustomer, rajStaff, "ORD-1001001",
                    "DELIVERED", 520.0, 93.6, 25.0, 588.6,
                    now.minusDays(8), now.minusDays(6),
                    "TXN-87234567", "COMPLETED", "CARD",
                    "INV-1001001",
                    List.of(
                            new ItemInput(washService, 2.5, 80.0, 320.0),
                            new ItemInput(dryCleanService, 1.0, 150.0, 200.0)
                    ));

            // ── Order 2: Amit — Dry Clean (READY) ──
            createOrder(amitCustomer, rajStaff, "ORD-1001002",
                    "READY", 300.0, 54.0, 0.0, 354.0,
                    now.minusDays(3), now.plusDays(1),
                    "TXN-87234568", "PENDING", "CASH",
                    "INV-1001002",
                    List.of(
                            new ItemInput(dryCleanService, 2.0, 150.0, 300.0)
                    ));

            // ── Order 3: Neha — Premium + Wash (WASHING) ──
            createOrder(nehaCustomer, null, "ORD-1002001",
                    "WASHING", 680.0, 122.4, 50.0, 752.4,
                    now.minusDays(1), now.plusDays(1),
                    "TXN-97234567", "PENDING", "UPI",
                    "INV-1002001",
                    List.of(
                            new ItemInput(premiumService, 1.5, 200.0, 600.0),
                            new ItemInput(washFoldService, 2.0, 100.0, 350.0)
                    ));

            // ── Order 4: Neha — Ironing Only (PICKED_UP) ──
            createOrder(nehaCustomer, priyaStaff, "ORD-1002002",
                    "PICKED_UP", 100.0, 18.0, 0.0, 118.0,
                    now, now.plusDays(2),
                    "TXN-97234568", "PENDING", "CASH",
                    "INV-1002002",
                    List.of(
                            new ItemInput(ironService, 1.0, 40.0, 100.0)
                    ));

            // ── Order 5: Amit — Wash & Fold (PENDING) ──
            createOrder(amitCustomer, null, "ORD-1001003",
                    "PENDING", 450.0, 81.0, 30.0, 501.0,
                    now.plusDays(1), now.plusDays(3),
                    null, "PENDING", "CASH",
                    "INV-1001003",
                    List.of(
                            new ItemInput(washFoldService, 3.0, 100.0, 450.0)
                    ));

            // ── Order 6: Neha — Full Combo (OUT_FOR_DELIVERY) ──
            createOrder(nehaCustomer, rajStaff, "ORD-1002003",
                    "OUT_FOR_DELIVERY", 760.0, 136.8, 100.0, 796.8,
                    now.minusDays(5), now,
                    "TXN-97234569", "COMPLETED", "CARD",
                    "INV-1002003",
                    List.of(
                            new ItemInput(premiumService, 1.0, 200.0, 500.0),
                            new ItemInput(washService, 3.0, 80.0, 360.0),
                            new ItemInput(ironService, 2.0, 40.0, 140.0)
                    ));

            log.info("Created first 6 demo orders");

            // ──────────────────────────────────────────────
            // 5. MORE DEMO ORDERS — spread across past months for richer charts
            // ──────────────────────────────────────────────

            // ── Order 7: Amit — Standard Wash (DELIVERED, 2 months ago) ──
            createOrder(amitCustomer, rajStaff, "ORD-1001004",
                    "DELIVERED", 280.0, 50.4, 0.0, 330.4,
                    now.minusMonths(2).minusDays(5), now.minusMonths(2).minusDays(3),
                    "TXN-87234570", "COMPLETED", "CASH",
                    "INV-1001004",
                    List.of(
                            new ItemInput(washService, 2.0, 80.0, 280.0)
                    ));

            // ── Order 8: Neha — Premium Care (DELIVERED, 2 months ago) ──
            createOrder(nehaCustomer, rajStaff, "ORD-1002004",
                    "DELIVERED", 500.0, 90.0, 0.0, 590.0,
                    now.minusMonths(2).minusDays(2), now.minusMonths(2),
                    "TXN-97234570", "COMPLETED", "UPI",
                    "INV-1002004",
                    List.of(
                            new ItemInput(premiumService, 1.0, 200.0, 500.0)
                    ));

            // ── Order 9: Amit — Wash & Fold (DELIVERED, 1 month ago) ──
            createOrder(amitCustomer, priyaStaff, "ORD-1001005",
                    "DELIVERED", 350.0, 63.0, 0.0, 413.0,
                    now.minusMonths(1).minusDays(10), now.minusMonths(1).minusDays(8),
                    "TXN-87234571", "COMPLETED", "CARD",
                    "INV-1001005",
                    List.of(
                            new ItemInput(washFoldService, 2.0, 100.0, 350.0)
                    ));

            // ── Order 10: Neha — Dry Clean (DELIVERED, 1 month ago) ──
            createOrder(nehaCustomer, rajStaff, "ORD-1002005",
                    "DELIVERED", 650.0, 117.0, 0.0, 767.0,
                    now.minusMonths(1).minusDays(5), now.minusMonths(1).minusDays(3),
                    "TXN-97234571", "COMPLETED", "CARD",
                    "INV-1002005",
                    List.of(
                            new ItemInput(dryCleanService, 3.0, 150.0, 650.0)
                    ));

            // ── Order 11: Amit — Premium + Ironing (DELIVERED, 3 weeks ago) ──
            createOrder(amitCustomer, priyaStaff, "ORD-1001006",
                    "DELIVERED", 460.0, 82.8, 20.0, 522.8,
                    now.minusWeeks(3), now.minusWeeks(3).plusDays(2),
                    "TXN-87234572", "COMPLETED", "UPI",
                    "INV-1001006",
                    List.of(
                            new ItemInput(premiumService, 0.5, 200.0, 400.0),
                            new ItemInput(ironService, 1.5, 40.0, 60.0)
                    ));

            // ── Order 12: Neha — Standard Wash (CANCELLED, 2 weeks ago) ──
            createOrder(nehaCustomer, null, "ORD-1002006",
                    "CANCELLED", 200.0, 36.0, 0.0, 236.0,
                    now.minusWeeks(2), now.minusWeeks(2).plusDays(1),
                    "TXN-97234572", "REFUNDED", "CARD",
                    "INV-1002006",
                    List.of(
                            new ItemInput(washService, 1.0, 80.0, 200.0)
                    ));

            // ── Order 13: Amit — Wash & Fold + Dry Clean (DELIVERED, 1 week ago) ──
            createOrder(amitCustomer, rajStaff, "ORD-1001007",
                    "DELIVERED", 720.0, 129.6, 50.0, 799.6,
                    now.minusWeeks(1).minusDays(2), now.minusWeeks(1),
                    "TXN-87234573", "COMPLETED", "CARD",
                    "INV-1001007",
                    List.of(
                            new ItemInput(washFoldService, 2.0, 100.0, 350.0),
                            new ItemInput(dryCleanService, 2.0, 150.0, 500.0)
                    ));

            // ── Order 14: Neha — Wash & Fold (IRONING, this week) ──
            createOrder(nehaCustomer, priyaStaff, "ORD-1002007",
                    "IRONING", 350.0, 63.0, 0.0, 413.0,
                    now.minusDays(2), now.plusDays(1),
                    "TXN-97234573", "PENDING", "CASH",
                    "INV-1002007",
                    List.of(
                            new ItemInput(washFoldService, 2.0, 100.0, 350.0)
                    ));

            log.info("Created 14 demo orders total (8 additional across past months)");
            log.info("===== DATA SEEDING COMPLETE =====");

        } catch (Exception e) {
            log.error("Error during data seeding", e);
            throw e;
        }
    }

    // ──────────────────────────────────────────────
    // HELPER — Create Order with Items, Payment, Invoice
    // ──────────────────────────────────────────────

    private void createOrder(Customer customer, Staff staff, String orderNumber,
                             String status, Double totalAmount, Double tax, Double discount,
                             Double finalAmount, LocalDateTime pickupDate, LocalDateTime deliveryDate,
                             String transactionId, String paymentStatus, String paymentMethod,
                             String invoiceNumber, List<ItemInput> items) {

        LaundryOrder order = LaundryOrder.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .staff(staff)
                .status(status)
                .totalAmount(totalAmount)
                .tax(tax)
                .discount(discount)
                .finalAmount(finalAmount)
                .pickupDate(pickupDate)
                .deliveryDate(deliveryDate)
                .build();

        for (ItemInput item : items) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .service(item.service())
                    .quantity(item.quantity())
                    .unitPrice(item.unitPrice())
                    .subTotal(item.subTotal())
                    .build();
            order.addOrderItem(orderItem);
        }

        LaundryOrder savedOrder = orderRepository.save(order);

        // Create Payment
        if (transactionId == null) {
            transactionId = "TXN-" + System.currentTimeMillis() % 10000000 + new Random().nextInt(100);
        }
        Payment payment = Payment.builder()
                .order(savedOrder)
                .transactionId(transactionId)
                .amount(finalAmount)
                .status(paymentStatus)
                .method(paymentMethod)
                .build();
        paymentRepository.save(payment);
        savedOrder.setPayment(payment);

        // Create Invoice
        Invoice invoice = Invoice.builder()
                .order(savedOrder)
                .invoiceNumber(invoiceNumber)
                .issuedDate(pickupDate)
                .build();
        invoiceRepository.save(invoice);
        savedOrder.setInvoice(invoice);

        // Create Notification
        String statusMsg = switch (status) {
            case "PENDING" -> "has been placed and is awaiting pickup.";
            case "PICKED_UP" -> "has been picked up and is at our facility.";
            case "WASHING" -> "is currently being washed.";
            case "IRONING" -> "is being ironed and finished.";
            case "READY" -> "is ready for delivery!";
            case "OUT_FOR_DELIVERY" -> "is out for delivery!";
            case "DELIVERED" -> "has been delivered. Thank you for choosing AquaClean Luxe!";
            case "CANCELLED" -> "has been cancelled.";
            default -> "status updated to: " + status;
        };

        Notification notification = Notification.builder()
                .user(customer.getUser())
                .message("Order " + orderNumber + " " + statusMsg +
                        " Expected delivery: " + deliveryDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    // Simple record for item input
    private record ItemInput(com.laundry.system.entity.Service service,
                             Double quantity, Double unitPrice, Double subTotal) {}
}
