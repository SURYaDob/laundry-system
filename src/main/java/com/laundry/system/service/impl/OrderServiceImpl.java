package com.laundry.system.service.impl;

import com.laundry.system.dto.OrderItemDto;
import com.laundry.system.dto.OrderPlacementDto;
import com.laundry.system.entity.*;
import com.laundry.system.exception.BusinessRuleException;
import com.laundry.system.exception.ResourceNotFoundException;
import com.laundry.system.repository.*;
import com.laundry.system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    private final LaundryOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final StaffRepository staffRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public OrderServiceImpl(LaundryOrderRepository orderRepository, CustomerRepository customerRepository,
                            ServiceRepository serviceRepository, StaffRepository staffRepository,
                            PaymentRepository paymentRepository, InvoiceRepository invoiceRepository,
                            NotificationRepository notificationRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.staffRepository = staffRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public LaundryOrder placeOrder(String customerEmail, OrderPlacementDto dto) {
        Customer customer = customerRepository.findByUserEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", customerEmail));

        // 1. Generate Order Number
        String orderNumber = "ORD-" + System.currentTimeMillis() % 10000000 + new Random().nextInt(100);

        // 2. Initialize Order
        LaundryOrder order = LaundryOrder.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .status("PENDING")
                .totalAmount(0.0)
                .tax(0.0)
                .discount(dto.getDiscount())
                .finalAmount(0.0)
                .pickupDate(LocalDateTime.parse(dto.getPickupDate(), DateTimeFormatter.ISO_DATE_TIME))
                .deliveryDate(LocalDateTime.parse(dto.getPickupDate(), DateTimeFormatter.ISO_DATE_TIME).plusDays(2)) // Standard 2 days delivery
                .build();

        double totalItemCost = 0.0;

        // 3. Process Order Items
        for (OrderItemDto itemDto : dto.getItems()) {
            com.laundry.system.entity.Service service = serviceRepository.findById(itemDto.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service", "id", itemDto.getServiceId()));

            // Formula: subTotal = basePrice + (pricePerKg * quantity)
            double subTotal = service.getBasePrice() + (service.getPricePerKg() * itemDto.getQuantity());
            totalItemCost += subTotal;

            OrderItem item = OrderItem.builder()
                    .service(service)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(service.getPricePerKg())
                    .subTotal(subTotal)
                    .build();

            order.addOrderItem(item);
        }

        // 4. Billing calculation
        double tax = totalItemCost * 0.18; // 18% GST standard
        double finalAmount = Math.max(0.0, totalItemCost + tax - dto.getDiscount());

        order.setTotalAmount(totalItemCost);
        order.setTax(tax);
        order.setFinalAmount(finalAmount);

        // Save order first so it generates ID
        LaundryOrder savedOrder = orderRepository.save(order);

        // 5. Create Payment Linkage
        Payment payment = Payment.builder()
                .order(savedOrder)
                .transactionId("TXN-" + System.currentTimeMillis() % 10000000)
                .amount(finalAmount)
                .status("PENDING")
                .method("CASH")
                .build();
        paymentRepository.save(payment);
        savedOrder.setPayment(payment);

        // 6. Create Invoice entry
        Invoice invoice = Invoice.builder()
                .order(savedOrder)
                .invoiceNumber("INV-" + System.currentTimeMillis() % 10000000)
                .issuedDate(LocalDateTime.now())
                .build();
        invoiceRepository.save(invoice);
        savedOrder.setInvoice(invoice);

        // 7. Create User notification
        Notification notification = Notification.builder()
                .user(customer.getUser())
                .message("Your laundry order " + orderNumber + " has been booked successfully! Expected delivery: " + savedOrder.getDeliveryDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LaundryOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public LaundryOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public LaundryOrder getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
    }

    @Override
    @Transactional
    public LaundryOrder updateOrderStatus(Long orderId, String status) {
        LaundryOrder order = getOrderById(orderId);
        order.setStatus(status);

        // Auto update Payment to completed when order is Delivered
        if ("DELIVERED".equals(status) && order.getPayment() != null) {
            order.getPayment().setStatus("COMPLETED");
        }

        LaundryOrder savedOrder = orderRepository.save(order);

        // Notify customer about status shift
        Notification notification = Notification.builder()
                .user(order.getCustomer().getUser())
                .message("Order " + order.getOrderNumber() + " status updated to: " + status)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        return savedOrder;
    }

    @Override
    @Transactional
    public LaundryOrder assignStaff(Long orderId, Long staffId) {
        LaundryOrder order = getOrderById(orderId);
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        order.setStaff(staff);
        LaundryOrder savedOrder = orderRepository.save(order);

        // Notify customer
        Notification custNotif = Notification.builder()
                .user(order.getCustomer().getUser())
                .message("Staff member " + staff.getUser().getFullName() + " has been assigned to your order " + order.getOrderNumber())
                .isRead(false)
                .build();
        notificationRepository.save(custNotif);

        // Notify staff member
        Notification staffNotif = Notification.builder()
                .user(staff.getUser())
                .message("New job assigned! Order number " + order.getOrderNumber() + " is ready for pick-up/processing.")
                .isRead(false)
                .build();
        notificationRepository.save(staffNotif);

        return savedOrder;
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        LaundryOrder order = getOrderById(id);
        orderRepository.delete(order);
    }
}
