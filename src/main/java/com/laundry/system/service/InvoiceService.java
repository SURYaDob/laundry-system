package com.laundry.system.service;

public interface InvoiceService {
    byte[] generateInvoicePdf(Long orderId);
}
