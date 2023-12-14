package com.dormitory.backend.api;

import com.dormitory.backend.pojo.Notification;
import com.dormitory.backend.pojo.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Notification findById(long id);
    List<Notification> findByReceiver(user receiver);


}
