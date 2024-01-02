package com.dormitory.backend.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.springframework.cache.annotation.Cacheable;

@Entity(name = "subject")
@Table(schema = "public")
@Schema
public class Subject {
    @Id
    @Column(name = "subject_id")
    @Schema
    String id;

    @Column(unique = true,nullable = false)
    @Schema
    String name;
    

    @Cacheable("Subject_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
