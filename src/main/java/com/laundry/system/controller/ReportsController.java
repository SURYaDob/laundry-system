package com.laundry.system.controller;

import com.laundry.system.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class ReportsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public ReportsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics")
    public String redirectAnalytics() {
        return "redirect:/admin/reports";
    }

    @GetMapping("/reports")
    public String viewAnalyticsReports(Model model) {
        Map<String, Object> analyticsData = analyticsService.getAdminAnalyticsData();
        model.addAllAttributes(analyticsData);
        return "reports/analytics";
    }
}
