package com.gfc.domain.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "ingredientes")
public class Ingrediente extends BaseEntity {
    protected Ingrediente() { }

    @Column(nullable = false)
    private String nombreIngrediente;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadMedida unidadMedida;
    private int stockActual;

    public Ingrediente(Long id, String nombreIngrediente, int stockActual, UnidadMedida unidadMedida) {
        super(id);
        this.nombreIngrediente = Validaciones.texto(nombreIngrediente, "nombreIngrediente");
        this.stockActual = Validaciones.noNegativo(stockActual, "stockActual");
        this.unidadMedida = Objects.requireNonNull(unidadMedida, "unidadMedida");
    }

    public void aumentarStock(int cantidad) {
        stockActual = Math.addExact(stockActual, Validaciones.positivo(cantidad, "cantidad"));
    }

    public void disminuirStock(int cantidad) {
        Validaciones.positivo(cantidad, "cantidad");
        if (!hayStock(cantidad)) {
            throw new IllegalStateException("Stock insuficiente para " + nombreIngrediente);
        }
        stockActual -= cantidad;
    }

    public boolean hayStock(int cantidad) {
        return stockActual >= Validaciones.noNegativo(cantidad, "cantidad");
    }

    public Long getIdIngrediente() { return getId(); }
    public String getNombreIngrediente() { return nombreIngrediente; }
    public int getStockActual() { return stockActual; }
    public UnidadMedida getUnidadMedida() { return unidadMedida; }
}
