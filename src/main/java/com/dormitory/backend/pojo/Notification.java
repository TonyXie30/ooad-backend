package com.dormitory.backend.pojo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

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
    private User sender;


    @ManyToOne
    @JoinColumn(name = "receiver_id",referencedColumnName = "user_id")
    @Schema
    private User receiver;

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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
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
