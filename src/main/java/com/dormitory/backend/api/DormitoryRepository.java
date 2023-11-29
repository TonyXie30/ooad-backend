package com.dormitory.backend.api;

import com.dormitory.backend.pojo.dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface DormitoryRepository extends JpaRepository<dormitory, Long>, JpaSpecificationExecutor<dormitory> {
    int findByBed(dormitory dormitory);
    int findByBookedNum(dormitory dormitory);
    dormitory findById(int id);
}
