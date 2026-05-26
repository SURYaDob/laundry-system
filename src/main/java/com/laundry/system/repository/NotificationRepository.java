package com.laundry.system.repository;

import com.laundry.system.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedDateDesc(Long userId);
    List<Notification> findByUserIdAndIsReadOrderByCreatedDateDesc(Long userId, Boolean isRead);
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
}
