package com.dormitory.backend.api;

import com.dormitory.backend.pojo.comment;
import com.dormitory.backend.pojo.dormitory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<comment,Long> {
    comment findById(int id);
    List<comment> findByDormitoryAndParent(dormitory dorm,comment parent);
}
