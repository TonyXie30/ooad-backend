package com.dormitory.backend.api;

import com.dormitory.backend.pojo.dormitory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.awt.print.Pageable;

public class DormitorySpecifications {

    public static Specification<dormitory> findByCriteria(
            String houseNum, Integer floor, String buildingName, String location) {
        return (root, query, criteriaBuilder) -> {
            // 构建查询条件

            Predicate predicate = criteriaBuilder.conjunction(); // 使用 conjunction 表示 AND 关系

            // 示例：如果 houseNum 不为空，添加对 houseNum 字段的查询条件
            if (houseNum != null && !houseNum.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("houseNum"), houseNum));
            }

            // 示例：如果 floor 不为空，添加对 floor 字段的查询条件
            if (floor != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("floor"), floor));
            }

            // 示例：如果 buildingName 不为空，添加对 buildingName 字段的查询条件
            if (buildingName != null && !buildingName.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("buildingName"), buildingName));
            }

            // 示例：如果 location 不为空，添加对 location 字段的查询条件
            if (location != null && !location.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("location"), location));
            }



            // 如果所有字段都为空，不添加额外条件，查询全部数据
            if (predicate.getExpressions().isEmpty()) {
                return query.getRestriction();
            }

            // 添加查询条件
            query.where(predicate);

            // 返回查询条件
            return query.getRestriction();
        };
    }
}
