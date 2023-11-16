package com.dormitory.backend.pojo;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.List;

@Entity(name = "timeRange")
@Table(schema = "public")
public class timeRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeslot_id")
    int id;
    @Column(name = "timeslot")
    Time timeSlot;
    @ManyToMany(mappedBy = "bedtime")
    List<user> bedtime_Users;
    @ManyToMany(mappedBy = "wakeupTime")
    List<user> uptime_Users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Time getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Time timeSlot) {
        this.timeSlot = timeSlot;
    }
}
