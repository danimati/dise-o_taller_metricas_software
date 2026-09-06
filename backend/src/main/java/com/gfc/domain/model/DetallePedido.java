package com.gfc.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "detalles_pedido")
public class DetallePedido extends BaseEntity {
    protected DetallePedido() { }

    private int cantidad;
    @ManyToOne(optional = false)
    @JoinColumn(name = "comida_id", nullable = false)
    private Comida comida;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal precioUnitario;
    @ManyToMany
    @JoinTable(name = "detalle_receta", joinColumns = @JoinColumn(name = "detalle_id"),
            inverseJoinColumns = @JoinColumn(name = "configuracion_id"))
    @OrderBy("id ASC")
    private List<ConfiguracionIngrediente> receta;
    @OneToMany(mappedBy = "detalle", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<PersonalizacionIngrediente> detallePedidoIngrediente = new ArrayList<>();
    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    public DetallePedido(Long id, Comida comida, int cantidad) {
        super(id);
        this.comida = Objects.requireNonNull(comida, "comida");
        this.cantidad = Validaciones.positivo(cantidad, "cantidad");
        this.precioUnitario = comida.getPrecio();
        this.receta = new ArrayList<>(comida.getIngredientes());
    }

    public void agregarPersonalizacion(PersonalizacionIngrediente personalizacion) {
        Objects.requireNonNull(personalizacion, "personalizacion");
        if (!receta.contains(personalizacion.getComidaIngrediente())) {
            throw new IllegalArgumentException("El ingrediente no pertenece a la receta del detalle");
        }
        if (detallePedidoIngrediente.stream().anyMatch(p ->
                p.getComidaIngrediente() == personalizacion.getComidaIngrediente())) {
            throw new IllegalArgumentException("Ya existe una personalizacion para este ingrediente");
        }
        personalizacion.vincular(this);
        detallePedidoIngrediente.add(personalizacion);
    }

    public void eliminarPersonalizacion(PersonalizacionIngrediente personalizacion) {
        if (!detallePedidoIngrediente.remove(Objects.requireNonNull(personalizacion, "personalizacion"))) {
            throw new IllegalArgumentException("La personalizacion no pertenece al detalle");
        }
        personalizacion.desvincular();
    }

    public BigDecimal calcularSubtotal() {
        BigDecimal adicionales = detallePedidoIngrediente.stream()
                .map(PersonalizacionIngrediente::calcularCostoAdicional)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return precioUnitario.add(adicionales).multiply(BigDecimal.valueOf(cantidad));
    }

    public boolean puedePrepararse() {
        return cantidadesNecesarias().entrySet().stream().allMatch(e -> e.getKey().hayStock(e.getValue()));
    }

    /** Cantidades totales en la unidad de medida de cada ingrediente; no descuenta stock. */
    public Map<Ingrediente, Integer> cantidadesNecesarias() {
        Map<Ingrediente, Integer> cantidades = new LinkedHashMap<>();
        for (ConfiguracionIngrediente configuracion : receta) {
            int unidades = configuracion.isIncluidoPorDefecto() ? configuracion.getCantidadBase() : 0;
            for (PersonalizacionIngrediente personalizacion : detallePedidoIngrediente) {
                if (personalizacion.getComidaIngrediente() == configuracion) {
                    unidades = personalizacion.getTipoAdicion() == TipoPersonalizacion.ADICIONAL
                            ? Math.addExact(unidades, personalizacion.getCantidad())
                            : Math.subtractExact(unidades, personalizacion.getCantidad());
                }
            }
            cantidades.put(configuracion.getIngrediente(), Math.multiplyExact(unidades, cantidad));
        }
        return Map.copyOf(cantidades);
    }

    void vincular(Pedido pedido) {
        if (this.pedido != null && this.pedido != pedido) {
            throw new IllegalArgumentException("El detalle pertenece a otro pedido");
        }
        this.pedido = pedido;
    }

    void desvincular() { pedido = null; }
    public int getCantidad() { return cantidad; }
    public Comida getComida() { return comida; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getSubtotal() { return calcularSubtotal(); }
    public List<PersonalizacionIngrediente> getDetallePedidoIngrediente() {
        return List.copyOf(detallePedidoIngrediente);
    }
}
