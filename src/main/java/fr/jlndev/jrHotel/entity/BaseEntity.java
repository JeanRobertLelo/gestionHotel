package fr.jlndev.jrHotel.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
