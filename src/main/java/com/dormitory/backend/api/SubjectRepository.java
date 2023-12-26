package com.dormitory.backend.api;

import com.dormitory.backend.pojo.Gender;
import com.dormitory.backend.pojo.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository
        extends JpaRepository<Subject,Long> {
    Subject findByName(String name);
    Subject findById(String id);
}
