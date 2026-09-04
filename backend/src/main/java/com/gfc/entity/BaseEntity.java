package com.gfc.entity;

import org.springframework.data.annotation.Id;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class BaseEntity {
    @Id
    private Long id;
}
