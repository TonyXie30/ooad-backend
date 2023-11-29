package com.dormitory.backend.api;

import com.dormitory.backend.pojo.dormitory;
import org.springframework.data.jpa.domain.Specification;

public class DormitorySpecifications {

    public static Specification<dormitory> findByCriteria(String houseNum, Integer floor, String buildingName, String location) {
        return (root, query, criteriaBuilder) -> {
            // 构建查询条件
            // 注意：在这里你可以根据传入的参数动态构建条件

            // 示例：如果 houseNum 不为空，添加对 houseNum 字段的查询条件
            if (houseNum != null && !houseNum.isEmpty()) {
                // 使用 criteriaBuilder 构建查询条件
                // root 表示实体的根，这里是 Dormitory
                // criteriaBuilder 提供了各种查询条件的构建方法
                query.where(criteriaBuilder.equal(root.get("houseNum"), houseNum));
            }

            // 示例：如果 floor 不为空，添加对 floor 字段的查询条件
            if (floor != null) {
                query.where(criteriaBuilder.equal(root.get("floor"), floor));
            }

            // 示例：如果 buildingName 不为空，添加对 buildingName 字段的查询条件
            if (buildingName != null && !buildingName.isEmpty()) {
                query.where(criteriaBuilder.equal(root.get("buildingName"), buildingName));
            }

            // 示例：如果 location 不为空，添加对 location 字段的查询条件
            if (location != null && !location.isEmpty()) {
                query.where(criteriaBuilder.equal(root.get("location"), location));
            }

            // 返回查询条件
            return query.getRestriction();
        };
    }
}
