package com.dormitory.backend.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.sql.Time;
import java.util.List;

@Entity(name = "time_range")
@Table(schema = "public")
@Schema
public class timeRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeslot_id")
    @Schema
    int id;
    @Column(name = "timeslot",unique = true)
    @Schema
    Time timeSlot;

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
