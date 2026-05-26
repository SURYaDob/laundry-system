package com.laundry.system.repository;

import com.laundry.system.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUserEmail(String email);
    Optional<Staff> findByUserId(Long userId);
    List<Staff> findByStatus(String status);
}
