package com.laundry.system.controller;

import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.entity.Staff;
import com.laundry.system.repository.LaundryOrderRepository;
import com.laundry.system.repository.StaffRepository;
import com.laundry.system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffOperationsController {

    private final OrderService orderService;
    private final StaffRepository staffRepository;
    private final LaundryOrderRepository orderRepository;

    @Autowired
    public StaffOperationsController(OrderService orderService, StaffRepository staffRepository,
                                     LaundryOrderRepository orderRepository) {
        this.orderService = orderService;
        this.staffRepository = staffRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/orders")
    public String listAssignedOrders(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Staff staff = staffRepository.findByUserEmail(auth.getName()).orElse(null);

        if (staff != null) {
            List<LaundryOrder> assignedOrders = orderRepository.findByStaffIdOrderByCreatedDateDesc(staff.getId());
            model.addAttribute("assignedOrders", assignedOrders);
        }
        return "dashboard/staff"; // Uses staff.html in dashboard folder
    }

    @GetMapping("/orders/{id}")
    public String viewAssignedOrderDetails(@PathVariable("id") Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Staff staff = staffRepository.findByUserEmail(auth.getName()).orElse(null);
        LaundryOrder order = orderService.getOrderById(id);

        // Security Check: Verify order is assigned to this staff member
        if (staff == null || order.getStaff() == null || !order.getStaff().getId().equals(staff.getId())) {
            return "redirect:/access-denied";
        }

        model.addAttribute("order", order);
        return "orders/staff-details";
    }

    @PostMapping("/orders/status")
    public String updateOrderProgress(@RequestParam("orderId") Long orderId,
                                      @RequestParam("status") String status,
                                      RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Staff staff = staffRepository.findByUserEmail(auth.getName()).orElse(null);
        LaundryOrder order = orderService.getOrderById(orderId);

        // Security check
        if (staff == null || order.getStaff() == null || !order.getStaff().getId().equals(staff.getId())) {
            return "redirect:/access-denied";
        }

        try {
            orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addAttribute("success", "Order status advanced to " + status + "!");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }

        return "redirect:/staff/orders/" + orderId;
    }
}
