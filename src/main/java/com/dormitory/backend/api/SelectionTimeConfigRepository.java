package com.dormitory.backend.api;

import com.dormitory.backend.pojo.SelectionTimeConfig;
import com.dormitory.backend.pojo.Degree;
import com.dormitory.backend.pojo.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SelectionTimeConfigRepository
        extends JpaRepository<SelectionTimeConfig,Long> {

    SelectionTimeConfig findByGenderAndDegree(Gender gender, Degree degree);

    @Query("from SelectionTimeConfig sf " +
            "where (sf.degree.degree = :degree or :degree = '') " +
            "and (sf.gender.gender = :gender or :gender = '')")
//    留意这里where语句里属性的调用(e.g. sf.degree.degree)，这是面向对象式的，而非直接指向数据库内。
    List<SelectionTimeConfig> getSelectionTimeConfigListByGenderAndDegree(String gender, String degree);

}
