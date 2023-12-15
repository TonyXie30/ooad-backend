package com.dormitory.backend.api;

import com.dormitory.backend.pojo.SelectionTimeConfig;
import com.dormitory.backend.pojo.Degree;
import com.dormitory.backend.pojo.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectionTimeConfigRepository
        extends JpaRepository<SelectionTimeConfig,Long> {

    SelectionTimeConfig findByGenderAndDegree(Gender gender, Degree degree);
}
