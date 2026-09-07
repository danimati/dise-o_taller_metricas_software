package com.gfc.domain.model;

import jakarta.persistence.*;

/** Identidad comun del dominio; null identifica un objeto todavia no persistido. */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    protected BaseEntity() { }

    protected BaseEntity(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo");
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
