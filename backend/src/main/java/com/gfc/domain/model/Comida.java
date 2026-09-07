package com.gfc.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "comidas")
public class Comida extends BaseEntity {
    protected Comida() { }

    private String imagenURL;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal precio;
    @OneToMany(mappedBy = "comida", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<ConfiguracionIngrediente> ingredientes = new ArrayList<>();

    public Comida(Long id, String nombre, BigDecimal precio, String imagenURL) {
        super(id);
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.precio = Validaciones.dinero(precio, "precio");
        this.imagenURL = imagenURL;
    }

    public ConfiguracionIngrediente configurarIngrediente(Long id, Ingrediente ingrediente,
            int cantidadBase, boolean incluidoPorDefecto, boolean permiteAdicional,
            boolean permiteRetirar, BigDecimal precioAdicional) {
        Objects.requireNonNull(ingrediente, "ingrediente");
        if (ingredientes.stream().anyMatch(c -> c.getIngrediente() == ingrediente
                || (ingrediente.getId() != null && ingrediente.getId().equals(c.getIngrediente().getId())))) {
            throw new IllegalArgumentException("El ingrediente ya esta configurado en esta comida");
        }
        ConfiguracionIngrediente configuracion = new ConfiguracionIngrediente(id, this, ingrediente,
                cantidadBase, incluidoPorDefecto, permiteAdicional, permiteRetirar, precioAdicional);
        ingredientes.add(configuracion);
        return configuracion;
    }

    public String getImagenURL() { return imagenURL; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public List<ConfiguracionIngrediente> getIngredientes() { return List.copyOf(ingredientes); }
}
