package com.laundry.system.service;

import com.laundry.system.dto.OrderPlacementDto;
import com.laundry.system.entity.LaundryOrder;
import java.util.List;

public interface OrderService {
    LaundryOrder placeOrder(String customerEmail, OrderPlacementDto dto);
    List<LaundryOrder> getAllOrders();
    LaundryOrder getOrderById(Long id);
    LaundryOrder getOrderByOrderNumber(String orderNumber);
    LaundryOrder updateOrderStatus(Long orderId, String status);
    LaundryOrder assignStaff(Long orderId, Long staffId);
    void deleteOrder(Long id);
}
