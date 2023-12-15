package com.dormitory.backend.api;

import com.dormitory.backend.pojo.Degree;
import com.dormitory.backend.pojo.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DegreeRepository
        extends JpaRepository<Degree,Long> {

    Degree findByDegree(String degree);
}
