package com.laundry.system.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.laundry.system.entity.LaundryOrder;
import com.laundry.system.entity.OrderItem;
import com.laundry.system.exception.ResourceNotFoundException;
import com.laundry.system.repository.LaundryOrderRepository;
import com.laundry.system.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final LaundryOrderRepository orderRepository;

    @Autowired
    public InvoiceServiceImpl(LaundryOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateInvoicePdf(Long orderId) {
        LaundryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // 1. Title/Header Banner
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new BaseColor(2, 132, 199)); // Ocean Blue
            Paragraph title = new Paragraph("AQUACLEAN LUXE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph subtitle = new Paragraph("Premium Eco-Friendly Laundry & Dry Clean Services", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(25);
            document.add(subtitle);

            // 2. Meta Info Grid (Invoice and Customer specs)
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setSpacingAfter(25);

            Font boldLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);

            // Left Cell: Invoice Information
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.addElement(new Paragraph("INVOICE DETAILS", boldLabelFont));
            leftCell.addElement(new Paragraph("Invoice No: " + (order.getInvoice() != null ? order.getInvoice().getInvoiceNumber() : "INV-N/A"), normalFont));
            leftCell.addElement(new Paragraph("Order No: " + order.getOrderNumber(), normalFont));
            leftCell.addElement(new Paragraph("Issued Date: " + (order.getInvoice() != null ? order.getInvoice().getIssuedDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "N/A"), normalFont));
            leftCell.addElement(new Paragraph("Status: " + order.getStatus(), normalFont));
            metaTable.addCell(leftCell);

            // Right Cell: Customer Details
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(new Paragraph("CUSTOMER METRICS", boldLabelFont));
            rightCell.addElement(new Paragraph("Name: " + order.getCustomer().getUser().getFullName(), normalFont));
            rightCell.addElement(new Paragraph("Phone: " + order.getCustomer().getPhoneNumber(), normalFont));
            rightCell.addElement(new Paragraph("Email: " + order.getCustomer().getUser().getEmail(), normalFont));
            rightCell.addElement(new Paragraph("Address: " + order.getCustomer().getAddress(), normalFont));
            metaTable.addCell(rightCell);

            document.add(metaTable);

            // 3. Divider Line
            Paragraph line = new Paragraph("----------------------------------------------------------------------------------------------------------------------------------", normalFont);
            line.setSpacingAfter(20);
            document.add(line);

            // 4. Items Table
            PdfPTable itemsTable = new PdfPTable(4);
            itemsTable.setWidthPercentage(100);
            itemsTable.setSpacingAfter(20);
            itemsTable.setWidths(new int[]{4, 2, 2, 2});

            // Table Headers
            PdfPCell h1 = new PdfPCell(new Phrase("Service Category", boldLabelFont));
            h1.setBackgroundColor(new BaseColor(241, 245, 249));
            h1.setPadding(8);
            itemsTable.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Unit Rate", boldLabelFont));
            h2.setBackgroundColor(new BaseColor(241, 245, 249));
            h2.setPadding(8);
            itemsTable.addCell(h2);

            PdfPCell h3 = new PdfPCell(new Phrase("Qty (Kg/Pcs)", boldLabelFont));
            h3.setBackgroundColor(new BaseColor(241, 245, 249));
            h3.setPadding(8);
            itemsTable.addCell(h3);

            PdfPCell h4 = new PdfPCell(new Phrase("Subtotal", boldLabelFont));
            h4.setBackgroundColor(new BaseColor(241, 245, 249));
            h4.setPadding(8);
            itemsTable.addCell(h4);

            // Item Rows
            for (OrderItem item : order.getOrderItems()) {
                itemsTable.addCell(new PdfPCell(new Phrase(item.getService().getName(), normalFont)));
                itemsTable.addCell(new PdfPCell(new Phrase("Rs." + String.format("%.2f", item.getUnitPrice()), normalFont)));
                itemsTable.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont)));
                itemsTable.addCell(new PdfPCell(new Phrase("Rs." + String.format("%.2f", item.getSubTotal()), normalFont)));
            }

            document.add(itemsTable);

            // 5. Invoice Totals Table
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(40);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPCell labelCell;
            PdfPCell valueCell;

            // Subtotal
            labelCell = new PdfPCell(new Paragraph("Services Subtotal:", normalFont));
            labelCell.setBorder(Rectangle.NO_BORDER);
            totalsTable.addCell(labelCell);
            valueCell = new PdfPCell(new Paragraph("Rs." + String.format("%.2f", order.getTotalAmount()), normalFont));
            valueCell.setBorder(Rectangle.NO_BORDER);
            valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.addCell(valueCell);

            // GST (18%)
            labelCell = new PdfPCell(new Paragraph("Taxes & GST (18%):", normalFont));
            labelCell.setBorder(Rectangle.NO_BORDER);
            totalsTable.addCell(labelCell);
            valueCell = new PdfPCell(new Paragraph("Rs." + String.format("%.2f", order.getTax()), normalFont));
            valueCell.setBorder(Rectangle.NO_BORDER);
            valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.addCell(valueCell);

            // Discount
            labelCell = new PdfPCell(new Paragraph("Discounts Applied:", normalFont));
            labelCell.setBorder(Rectangle.NO_BORDER);
            totalsTable.addCell(labelCell);
            valueCell = new PdfPCell(new Paragraph("-Rs." + String.format("%.2f", order.getDiscount()), normalFont));
            valueCell.setBorder(Rectangle.NO_BORDER);
            valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.addCell(valueCell);

            // Grand Total
            Font grandTotalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(79, 70, 229)); // Indigo
            labelCell = new PdfPCell(new Paragraph("Grand Total:", grandTotalFont));
            labelCell.setBorder(Rectangle.NO_BORDER);
            totalsTable.addCell(labelCell);
            valueCell = new PdfPCell(new Paragraph("Rs." + String.format("%.2f", order.getFinalAmount()), grandTotalFont));
            valueCell.setBorder(Rectangle.NO_BORDER);
            valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.addCell(valueCell);

            document.add(totalsTable);

            // 6. Thank You Footer
            Paragraph footer = new Paragraph("\n\n\nThank you for choosing AquaClean Luxe! Have a fresh day!\nFor support, contact support@aquacleanluxe.com", boldLabelFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
