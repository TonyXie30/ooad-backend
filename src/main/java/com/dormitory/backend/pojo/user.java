package com.dormitory.backend.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private int id;
    @Column(nullable = false,unique = true)
    @Schema
    private String username;
    @ManyToOne
    @JoinColumn(name = "gender",referencedColumnName = "gender")
    @Schema
    private Gender gender;
    @ManyToOne
    @JoinColumn(name = "degree",referencedColumnName = "degree")
    @Schema
    private Degree degree;
    @ManyToOne
    @JoinColumn(name = "subject_id",referencedColumnName = "subject_id")
    @Schema
    private Subject subject;
    @Column(nullable = false)
    @Schema
    private String password;
    @ManyToOne
    @JoinColumn(name = "bedtime",referencedColumnName = "timeslot")
    @Schema
    private timeRange bedtime;
    @ManyToOne
    @JoinColumn(name = "uptime",referencedColumnName = "timeslot")
    @Schema
    private timeRange uptime;
    @ManyToMany //@Fetch(FetchMode.JOIN)
    @JoinTable(name = "bookmark")
    @Schema
    private List<dormitory> bookmark;
    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    @Schema
    private dormitory bookedDormitory;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public timeRange getBedtime() {
        return bedtime;
    }

    public void setBedtime(timeRange bedtime) {
        this.bedtime = bedtime;
    }

    public timeRange getUptime() {
        return uptime;
    }

    public void setUptime(timeRange uptime) {
        this.uptime = uptime;
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
