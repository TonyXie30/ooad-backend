package com.dormitory.backend.pojo;

import com.dormitory.backend.pojo.Degree;
import com.dormitory.backend.pojo.Gender;
import com.dormitory.backend.pojo.timeRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Time;

@Entity
@Table(schema = "public",name = "selection_time_config")
@Schema
public class SelectionTimeConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selection_time_config_id")
    @Schema
    int id;
    @ManyToOne
    @JoinColumn(name = "gender",referencedColumnName = "gender")
    @Schema
    Gender gender;
    @ManyToOne
    @JoinColumn(name = "degree",referencedColumnName = "degree")
    @Schema
    Degree degree;

    @JoinColumn(name = "start_time")
    @Schema
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    Date startTime;

    @JoinColumn(name = "end_time")
    @Schema
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    Date endTime;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    //    YYYY-MM-DD hh:mm:ss
}
