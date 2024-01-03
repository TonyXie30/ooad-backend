package com.dormitory.backend.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cache.annotation.Cacheable;

@Entity(name = "Degree")
@Table(schema = "public")
@Schema
public class Degree {
    @Id
    @Column(unique = true)
    @Schema
    String degree;

    public Degree(String degree) {
        this.degree = degree;
    }
    public Degree(){}

    @Cacheable("Degree")
    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    @Override
    public String toString() {
        return degree;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return this.degree.equals(((Degree) obj).getDegree());
    }
}
