package com.dormitory.backend.pojo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.sql.Time;
import java.sql.Timestamp;

@Entity(name="Notification")
@Table(schema = "public")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id",referencedColumnName = "user_id")
    @Schema
    private user sender;


    @ManyToOne
    @JoinColumn(name = "receiver_id",referencedColumnName = "user_id")
    @Schema
    private user receiver;

    @Column(name = "content")
    @Schema
    String content;

    @Column(name = "time")
    @Schema
    private Timestamp time;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public user getSender() {
        return sender;
    }

    public void setSender(user sender) {
        this.sender = sender;
    }

    public user getReceiver() {
        return receiver;
    }

    public void setReceiver(user receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
