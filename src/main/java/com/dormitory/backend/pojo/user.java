package com.dormitory.backend.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.List;


@Entity(name = "users")
@Table(schema = "public")
@Schema
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Schema
    int id;
    @Column(nullable = false)
    @Schema
    String username;
    @Column(nullable = false)
    @Schema
    String password;
    @ManyToMany
    @JoinTable(name = "bookmark")
    @Schema
    private List<dormitory> bookmark;
    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    @Schema
    private dormitory bookedDormitory;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    @Schema
    private subject subject;
    @ManyToMany
    @JoinTable(name = "favour_bedtime")
    @Schema
    private List<timeRange> bedtime;
    @ManyToMany
    @JoinTable(name = "favour_wakeup_time")
    @Schema
    private List<timeRange> wakeupTime;

    @ManyToOne
    @JoinTable(name = "leader_id")
    @Schema
    private user leaderId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public user getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(user leaderId) {
        this.leaderId = leaderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<dormitory> getBookmark() {
        return bookmark;
    }

    public void setBookmark(List<dormitory> bookmark) {
        this.bookmark = bookmark;
    }

    public dormitory getBookedDormitory() {
        return bookedDormitory;
    }

    public void setBookedDormitory(dormitory bookedDormitory) {
        this.bookedDormitory = bookedDormitory;
    }

    public subject getSubject() {
        return subject;
    }

    public void setSubject(subject subject) {
        this.subject = subject;
    }

    public List<timeRange> getBedtime() {
        return bedtime;
    }

    public void setBedtime(List<timeRange> bedtime) {
        this.bedtime = bedtime;
    }

    public List<timeRange> getWakeupTime() {
        return wakeupTime;
    }

    public void setWakeupTime(List<timeRange> wakeupTime) {
        this.wakeupTime = wakeupTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void insertBookmark(dormitory dormitory){
        this.bookmark.add(dormitory);
    }
}
