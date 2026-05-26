package com.laundry.system.controller;

import com.laundry.system.dto.StaffDto;
import com.laundry.system.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/staff")
public class StaffController {

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String listStaff(Model model) {
        model.addAttribute("staffList", staffService.getAllStaff());
        model.addAttribute("staffForm", new StaffDto());
        return "staff/list";
    }

    @PostMapping("/new")
    public String addStaff(@Valid @ModelAttribute("staffForm") StaffDto staffDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("staffList", staffService.getAllStaff());
            return "staff/list";
        }

        try {
            staffService.createStaff(staffDto);
            redirectAttributes.addAttribute("success", "Staff member '" + staffDto.getFirstName() + "' added successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }

        return "redirect:/admin/staff";
    }

    @PostMapping("/delete/{id}")
    public String deactivateStaff(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            staffService.deleteStaff(id);
            redirectAttributes.addAttribute("success", "Staff member deactivated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }

        return "redirect:/admin/staff";
    }
}
