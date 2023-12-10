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
    private timeRange bedtime_start;
    @ManyToOne
    @JoinColumn(name = "bedtime_end",referencedColumnName = "timeslot")
    @Schema
    private timeRange bedtime_end;
    @ManyToOne
    @JoinColumn(name = "uptime_start",referencedColumnName = "timeslot")
    @Schema
    private timeRange uptime_start;
    @ManyToOne
    @JoinColumn(name = "uptime_end",referencedColumnName = "timeslot")
    @Schema
    private timeRange uptime_end;

    @ManyToOne
    @JoinTable(name = "leader_id")
    @Schema
    @JsonBackReference //防止json打印无限递归
    private user leaderId;
    @Column
    @Schema
    boolean admin;

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

    public timeRange getBedtime_start() {
        return bedtime_start;
    }

    public void setBedtime_start(timeRange bedtime_start) {
        this.bedtime_start = bedtime_start;
    }

    public timeRange getBedtime_end() {
        return bedtime_end;
    }

    public void setBedtime_end(timeRange bedtime_end) {
        this.bedtime_end = bedtime_end;
    }

    public timeRange getUptime_start() {
        return uptime_start;
    }

    public void setUptime_start(timeRange uptime_start) {
        this.uptime_start = uptime_start;
    }

    public timeRange getUptime_end() {
        return uptime_end;
    }

    public void setUptime_end(timeRange uptime_end) {
        this.uptime_end = uptime_end;
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
