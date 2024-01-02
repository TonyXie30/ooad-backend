package com.dormitory.backend.pojo;

public interface TeamSubject {
    void teamUp(User member, User leader);
    void kickMember(User user);
}
