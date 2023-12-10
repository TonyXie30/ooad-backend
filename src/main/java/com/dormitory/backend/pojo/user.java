package com.dormitory.backend.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    @Column(nullable = false)
    @Schema
    String gender;
    @ManyToMany //@Fetch(FetchMode.JOIN)
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
    @ManyToOne
    @JoinColumn(name = "bedtime_start",referencedColumnName = "timeslot")
    @Schema
    private timeRange bedtimeStart;
    @ManyToOne
    @JoinColumn(name = "bedtime_end",referencedColumnName = "timeslot")
    @Schema
    private timeRange bedtimeEnd;
    @ManyToOne
    @JoinColumn(name = "uptime_start",referencedColumnName = "timeslot")
    @Schema
    private timeRange uptimeStart;
    @ManyToOne
    @JoinColumn(name = "uptime_end",referencedColumnName = "timeslot")
    @Schema
    private timeRange uptimeEnd;

    @ManyToOne
    @JoinTable(name = "leader_id")
    @Schema
    @JsonBackReference //防止json打印无限递归
    private user leaderId;

    @Column
    @Schema
    private boolean admin;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public timeRange getBedtimeStart() {
        return bedtimeStart;
    }

    public void setBedtimeStart(timeRange bedtimeStart) {
        this.bedtimeStart = bedtimeStart;
    }

    public timeRange getBedtimeEnd() {
        return bedtimeEnd;
    }

    public void setBedtimeEnd(timeRange bedtimeEnd) {
        this.bedtimeEnd = bedtimeEnd;
    }

    public timeRange getUptimeStart() {
        return uptimeStart;
    }

    public void setUptimeStart(timeRange uptimeStart) {
        this.uptimeStart = uptimeStart;
    }

    public timeRange getUptimeEnd() {
        return uptimeEnd;
    }

    public void setUptimeEnd(timeRange uptimeEnd) {
        this.uptimeEnd = uptimeEnd;
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
