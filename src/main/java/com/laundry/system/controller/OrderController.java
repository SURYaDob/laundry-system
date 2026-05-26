package com.laundry.system.controller;

import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.service.OrderService;
import com.laundry.system.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    private final OrderService orderService;
    private final StaffRepository staffRepository;

    @Autowired
    public OrderController(OrderService orderService, StaffRepository staffRepository) {
        this.orderService = orderService;
        this.staffRepository = staffRepository;
    }

    @GetMapping
    public String listAllOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("staffList", staffRepository.findByStatus("ACTIVE"));
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String viewOrderDetails(@PathVariable("id") Long id, Model model) {
        try {
            LaundryOrder order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            return "orders/details";
        } catch (RuntimeException e) {
            return "redirect:/admin/orders?error=Order not found";
        }
    }

    @PostMapping("/assign")
    public String assignStaffToOrder(@RequestParam("orderId") Long orderId,
                                     @RequestParam("staffId") Long staffId,
                                     RedirectAttributes redirectAttributes) {
        try {
            orderService.assignStaff(orderId, staffId);
            redirectAttributes.addAttribute("success", "Staff member assigned successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("status") String status,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addAttribute("success", "Order status updated to " + status + "!");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrder(id);
            redirectAttributes.addAttribute("success", "Order deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }
}
