package com.dormitory.backend.pojo;

import java.util.List;

/**
 * 这个类暂时仅用于回传前端时的团队信息打包，不由spring管理。
 */
public class Team {
    List<User> members;
    User leader;
    Integer size;
    public Team(){}

    public Team(List<User> members, User leader, Integer size) {
        this.members = members;
        this.leader = leader;
        this.size = size;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
