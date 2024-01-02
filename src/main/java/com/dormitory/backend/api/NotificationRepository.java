package com.dormitory.backend.api;

import com.dormitory.backend.pojo.Notification;
import com.dormitory.backend.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Notification findById(long id);
    List<Notification> findByReceiver(User receiver);

    void deleteByReceiver(User receiver);

}
