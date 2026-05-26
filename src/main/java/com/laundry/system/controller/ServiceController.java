package com.laundry.system.controller;

import com.laundry.system.entity.Service;
import com.laundry.system.service.ServiceCrudService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/services")
public class ServiceController {

    private final ServiceCrudService serviceService;

    @Autowired
    public ServiceController(ServiceCrudService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public String listServices(Model model) {
        model.addAttribute("services", serviceService.getAllServices());
        model.addAttribute("serviceForm", new Service());
        return "services/list";
    }

    @PostMapping("/new")
    public String addService(@Valid @ModelAttribute("serviceForm") Service service,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("services", serviceService.getAllServices());
            return "services/list";
        }

        try {
            serviceService.createService(service);
            redirectAttributes.addAttribute("success", "Service '" + service.getName() + "' created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }

        return "redirect:/admin/services";
    }

    @PostMapping("/edit/{id}")
    public String editService(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("serviceForm") Service service,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "redirect:/admin/services?error=Validation failed";
        }

        try {
            serviceService.updateService(id, service);
            redirectAttributes.addAttribute("success", "Service updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }

        return "redirect:/admin/services";
    }

    @PostMapping("/delete/{id}")
    public String deleteService(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceService.deleteService(id);
            redirectAttributes.addAttribute("success", "Service deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }

        return "redirect:/admin/services";
    }
}
