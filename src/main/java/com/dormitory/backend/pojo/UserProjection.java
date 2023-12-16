package com.dormitory.backend.pojo;

public interface UserProjection {
    String getUsername();

    int getId();

    String getGender();

    String getDegree();

    String getBedTime();

    String getUpTime();

    String getSubject();
    // 添加其他需要的字段
}
