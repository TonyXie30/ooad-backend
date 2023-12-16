package com.dormitory.backend.api;

import com.dormitory.backend.pojo.comment;
import com.dormitory.backend.pojo.dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<comment,Long> {
    comment findById(int id);
    List<comment> findByDormitoryAndParent(dormitory dorm,comment parent);
    List<comment> findByDormitory(dormitory dormitory);
    List<comment> findByParent(comment parent);
    @Query("SELECT DISTINCT c FROM comment c join dormitory WHERE c.dormitory = :dormitory AND c.parent = c ORDER BY c.create_time DESC")
    List<comment> findFirstLevelComments(@Param("dormitory") dormitory dormitory);

}
