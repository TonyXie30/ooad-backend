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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
