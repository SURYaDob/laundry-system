package com.laundry.system.controller;

import com.laundry.system.entity.Customer;
import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.entity.Staff;
import com.laundry.system.entity.User;
import com.laundry.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final LaundryOrderRepository laundryOrderRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public DashboardController(UserRepository userRepository, CustomerRepository customerRepository,
                               StaffRepository staffRepository, LaundryOrderRepository laundryOrderRepository,
                               NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.laundryOrderRepository = laundryOrderRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_STAFF"))) {
            return "redirect:/staff/dashboard";
        } else if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_CUSTOMER"))) {
            return "redirect:/customer/dashboard";
        }

        return "redirect:/login?error=true";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName()).orElse(null);
        model.addAttribute("currentUser", currentUser);

        // Core Admin Stats
        model.addAttribute("totalOrders", laundryOrderRepository.count());
        model.addAttribute("totalRevenue", laundryOrderRepository.calculateTotalRevenue());
        model.addAttribute("pendingDeliveries", laundryOrderRepository.countByStatus("OUT_FOR_DELIVERY") + laundryOrderRepository.countByStatus("READY"));
        model.addAttribute("activeCustomers", customerRepository.count());

        // Recent Orders list
        List<LaundryOrder> recentOrders = laundryOrderRepository.findAll().stream()
                .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentOrders", recentOrders);

        return "dashboard/admin";
    }

    @GetMapping("/staff/dashboard")
    public String staffDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName()).orElse(null);
        model.addAttribute("currentUser", currentUser);

        Staff staff = staffRepository.findByUserEmail(auth.getName()).orElse(null);
        if (staff != null) {
            List<LaundryOrder> assignedOrders = laundryOrderRepository.findByStaffIdOrderByCreatedDateDesc(staff.getId());
            model.addAttribute("assignedOrders", assignedOrders);
            
            long pendingTasks = assignedOrders.stream().filter(o -> !"DELIVERED".equals(o.getStatus()) && !"CANCELLED".equals(o.getStatus())).count();
            model.addAttribute("pendingTasks", pendingTasks);
            
            long completedTasks = assignedOrders.stream().filter(o -> "DELIVERED".equals(o.getStatus())).count();
            model.addAttribute("completedTasks", completedTasks);
        }

        return "dashboard/staff";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName()).orElse(null);
        model.addAttribute("currentUser", currentUser);

        Customer customer = customerRepository.findByUserEmail(auth.getName()).orElse(null);
        if (customer != null) {
            List<LaundryOrder> myOrders = laundryOrderRepository.findByCustomerIdOrderByCreatedDateDesc(customer.getId());
            model.addAttribute("myOrders", myOrders);
            
            long unreadNotifications = notificationRepository.countByUserIdAndIsRead(currentUser.getId(), false);
            model.addAttribute("unreadNotifications", unreadNotifications);
        }

        return "dashboard/customer";
    }
}
