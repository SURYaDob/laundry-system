package com.laundry.system.controller;

import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.service.InvoiceService;
import com.laundry.system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final OrderService orderService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, OrderService orderService) {
        this.invoiceService = invoiceService;
        this.orderService = orderService;
    }

    @GetMapping("/customer/invoices/download/{orderId}")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable("orderId") Long orderId) {
        try {
            LaundryOrder order = orderService.getOrderById(orderId);
            byte[] pdfBytes = invoiceService.generateInvoicePdf(orderId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "invoice-" + order.getOrderNumber() + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
