package com.dormitory.backend.api;

import com.dormitory.backend.pojo.Dormitory;
import com.dormitory.backend.pojo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Comment findById(int id);
    List<Comment> findByDormitoryAndParent(Dormitory dorm, Comment parent);
    List<Comment> findByDormitory(Dormitory dormitory);
    List<Comment> findByParent(Comment parent);
    @Query("SELECT DISTINCT c FROM comment c join dormitory WHERE c.dormitory = :dormitory AND c.parent = c ORDER BY c.create_time DESC")
    List<Comment> findFirstLevelComments(@Param("Dormitory") Dormitory dormitory);

}
