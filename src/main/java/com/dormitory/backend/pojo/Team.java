package com.dormitory.backend.pojo;

import java.util.List;

/**
 * 这个类暂时仅用于回传前端时的团队信息打包，不由spring管理。
 */
public class Team {
    List<user> members;
    user leader;
    Integer size;
    public Team(){}

    public Team(List<user> members, user leader, Integer size) {
        this.members = members;
        this.leader = leader;
        this.size = size;
    }

    public List<user> getMembers() {
        return members;
    }

    public void setMembers(List<user> members) {
        this.members = members;
    }

    public user getLeader() {
        return leader;
    }

    public void setLeader(user leader) {
        this.leader = leader;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
