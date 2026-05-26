package com.laundry.system.controller;

import com.laundry.system.dto.OrderPlacementDto;
import com.laundry.system.entity.Customer;
import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.repository.CustomerRepository;
import com.laundry.system.repository.LaundryOrderRepository;
import com.laundry.system.repository.ServiceRepository;
import com.laundry.system.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final OrderService orderService;
    private final ServiceRepository serviceRepository;
    private final CustomerRepository customerRepository;
    private final LaundryOrderRepository orderRepository;

    @Autowired
    public CustomerController(OrderService orderService, ServiceRepository serviceRepository,
                              CustomerRepository customerRepository, LaundryOrderRepository orderRepository) {
        this.orderService = orderService;
        this.serviceRepository = serviceRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/orders")
    public String listMyOrders(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Customer customer = customerRepository.findByUserEmail(auth.getName()).orElse(null);
        
        if (customer != null) {
            List<LaundryOrder> myOrders = orderRepository.findByCustomerIdOrderByCreatedDateDesc(customer.getId());
            model.addAttribute("myOrders", myOrders);
        }
        return "orders/customer-list";
    }

    @GetMapping("/orders/new")
    public String showOrderPlacementForm(Model model) {
        model.addAttribute("orderForm", new OrderPlacementDto());
        model.addAttribute("catalog", serviceRepository.findAll());
        return "orders/new";
    }

    @PostMapping("/orders/new")
    public String placeLaundryOrder(@Valid @ModelAttribute("orderForm") OrderPlacementDto orderPlacementDto,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (result.hasErrors()) {
            model.addAttribute("catalog", serviceRepository.findAll());
            return "orders/new";
        }

        try {
            orderService.placeOrder(auth.getName(), orderPlacementDto);
            redirectAttributes.addAttribute("success", "Laundry pickup scheduled successfully!");
            return "redirect:/customer/orders";
        } catch (RuntimeException e) {
            model.addAttribute("catalog", serviceRepository.findAll());
            model.addAttribute("error", e.getMessage());
            return "orders/new";
        }
    }
}
