package com.dormitory.backend.pojo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity(name = "comment")
@Table(schema = "public")
@Schema
public class comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    @Schema
    int id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "comment_id",name = "parent_id")
    @Schema
    comment parent;
    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    @Schema
    dormitory dormitory;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema
    user user;
    @Column
    @Schema
    String content;


}
