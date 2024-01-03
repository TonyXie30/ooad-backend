package com.dormitory.backend.api;

import com.dormitory.backend.pojo.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepository
        extends JpaRepository<Gender,Long> {

    Gender findByGender(String gender);
}
