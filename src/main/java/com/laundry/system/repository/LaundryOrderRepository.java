package com.laundry.system.repository;

import com.laundry.system.entity.LaundryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaundryOrderRepository extends JpaRepository<LaundryOrder, Long> {
    Optional<LaundryOrder> findByOrderNumber(String orderNumber);
    List<LaundryOrder> findByCustomerIdOrderByCreatedDateDesc(Long customerId);
    List<LaundryOrder> findByStaffIdOrderByCreatedDateDesc(Long staffId);
    List<LaundryOrder> findByStatusOrderByCreatedDateDesc(String status);
    
    long countByStatus(String status);
    
    @Query("SELECT COALESCE(SUM(o.finalAmount), 0.0) FROM LaundryOrder o WHERE o.status != 'CANCELLED'")
    Double calculateTotalRevenue();
}
