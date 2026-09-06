package com.gfc.domain.model;

import com.gfc.domain.port.CodificadorContrasena;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DominioTest {
    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 9, 6, 12, 0);
    // Doble exclusivo de pruebas; el adaptador real tiene su propia prueba.
    private final CodificadorContrasena codificador = new CodificadorContrasena() {
        public String codificar(String texto) { return "test:" + texto; }
        public boolean comprobar(String texto, String hash) { return hash.equals(codificar(texto)); }
    };

    private Cliente cliente() {
        return new Cliente(1L, "ana", "secreto", "123", "ana@example.com", "Ana", codificador);
    }

    private Comida comida() {
        return new Comida(null, "Hamburguesa", new BigDecimal("10.10"), null);
    }

    private ConfiguracionIngrediente configurar(Comida comida, Ingrediente ingrediente) {
        return comida.configurarIngrediente(null, ingrediente, 2, true, true, true, new BigDecimal("0.20"));
    }

    @Test
    void calculaImportesExactosYActualizaElTotalAlCambiarPersonalizaciones() {
        Comida comida = comida();
        ConfiguracionIngrediente queso = configurar(comida, new Ingrediente(null, "Queso", 20, UnidadMedida.UNIDAD));
        DetallePedido detalle = new DetallePedido(null, comida, 3);
        Pedido pedido = new Pedido(null, cliente(), FECHA, detalle);
        PersonalizacionIngrediente extra = new PersonalizacionIngrediente(null, queso, 2, TipoPersonalizacion.ADICIONAL);
        detalle.agregarPersonalizacion(extra);
        assertEquals(new BigDecimal("0.40"), extra.calcularCostoAdicional());
        assertEquals(new BigDecimal("31.50"), pedido.getValorTotal());
        detalle.eliminarPersonalizacion(extra);
        assertEquals(new BigDecimal("30.30"), pedido.calcularTotal());
        DetallePedido segundo = new DetallePedido(null, comida, 1);
        pedido.agregarDetalle(segundo);
        assertEquals(new BigDecimal("40.40"), pedido.calcularTotal());
        pedido.eliminarDetalle(segundo);
        assertEquals(new BigDecimal("30.30"), pedido.calcularTotal());
    }

    @Test
    void retiroReduceConsumoSinDescuentoYLaConsultaNoConsumeStock() {
        Comida comida = comida();
        Ingrediente queso = new Ingrediente(null, "Queso", 3, UnidadMedida.UNIDAD);
        ConfiguracionIngrediente configuracion = configurar(comida, queso);
        DetallePedido detalle = new DetallePedido(null, comida, 3);
        assertFalse(detalle.puedePrepararse());
        detalle.agregarPersonalizacion(new PersonalizacionIngrediente(null, configuracion, 1, TipoPersonalizacion.RETIRADO));
        assertTrue(detalle.puedePrepararse());
        assertEquals(3, detalle.cantidadesNecesarias().get(queso));
        assertEquals(3, queso.getStockActual());
        assertEquals(new BigDecimal("30.30"), detalle.calcularSubtotal());
    }

    @Test
    void cuentaOpcionalesSoloCuandoSeAgreganYMultiplicaPorCantidadDeComidas() {
        Comida comida = comida();
        Ingrediente salsa = new Ingrediente(null, "Salsa", 3, UnidadMedida.UNIDAD);
        ConfiguracionIngrediente opcional = comida.configurarIngrediente(null, salsa, 1, false, true, false, BigDecimal.ONE);
        DetallePedido detalle = new DetallePedido(null, comida, 2);
        assertEquals(0, detalle.cantidadesNecesarias().get(salsa));
        detalle.agregarPersonalizacion(new PersonalizacionIngrediente(null, opcional, 2, TipoPersonalizacion.ADICIONAL));
        assertFalse(detalle.puedePrepararse());
        assertEquals(4, detalle.cantidadesNecesarias().get(salsa));
    }

    @Test
    void disponibilidadDePedidoAcumulaIngredientesCompartidos() {
        Ingrediente queso = new Ingrediente(2L, "Queso", 3, UnidadMedida.UNIDAD);
        Comida comida = comida();
        configurar(comida, queso);
        DetallePedido primero = new DetallePedido(null, comida, 1);
        DetallePedido segundo = new DetallePedido(null, comida, 1);
        Pedido pedido = new Pedido(null, cliente(), FECHA, primero);
        pedido.agregarDetalle(segundo);
        assertTrue(primero.puedePrepararse());
        assertTrue(segundo.puedePrepararse());
        assertFalse(pedido.puedePrepararse());
        queso.aumentarStock(1);
        assertTrue(pedido.puedePrepararse());
    }

    @Test
    void acumulaTambienInstanciasConLaMismaIdentidadPersistida() {
        Comida primera = comida();
        Comida segunda = comida();
        configurar(primera, new Ingrediente(2L, "Queso", 3, UnidadMedida.UNIDAD));
        configurar(segunda, new Ingrediente(2L, "Queso", 3, UnidadMedida.UNIDAD));
        Pedido pedido = new Pedido(null, cliente(), FECHA, new DetallePedido(null, primera, 1));
        pedido.agregarDetalle(new DetallePedido(null, segunda, 1));
        assertFalse(pedido.puedePrepararse());
    }

    @Test
    void rechazaPersonalizacionesAjenasDuplicadasYRetiroExcesivo() {
        Comida comida = comida();
        Ingrediente queso = new Ingrediente(null, "Queso", 10, UnidadMedida.UNIDAD);
        ConfiguracionIngrediente configuracion = configurar(comida, queso);
        DetallePedido detalle = new DetallePedido(null, comida, 1);
        assertThrows(IllegalArgumentException.class, () -> new PersonalizacionIngrediente(null, configuracion, 3, TipoPersonalizacion.RETIRADO));
        PersonalizacionIngrediente extra = new PersonalizacionIngrediente(null, configuracion, 1, TipoPersonalizacion.ADICIONAL);
        detalle.agregarPersonalizacion(extra);
        assertThrows(IllegalArgumentException.class, () -> detalle.agregarPersonalizacion(extra));
        DetallePedido otro = new DetallePedido(null, comida(), 1);
        assertThrows(IllegalArgumentException.class, () -> otro.agregarPersonalizacion(extra));
        DetallePedido mismaComida = new DetallePedido(null, comida, 1);
        assertThrows(IllegalArgumentException.class, () -> mismaComida.agregarPersonalizacion(extra));
        detalle.eliminarPersonalizacion(extra);
        mismaComida.agregarPersonalizacion(extra);
        assertEquals(1, mismaComida.getDetallePedidoIngrediente().size());
    }

    @Test
    void respetaPermisosDeLaRecetaYSuUnicidad() {
        Comida comida = comida();
        Ingrediente carne = new Ingrediente(2L, "Carne", 10, UnidadMedida.UNIDAD);
        ConfiguracionIngrediente fijo = comida.configurarIngrediente(null, carne, 1, true, false, false, BigDecimal.ZERO);
        assertFalse(fijo.permitePersonalizacion(TipoPersonalizacion.ADICIONAL));
        assertFalse(fijo.permitePersonalizacion(TipoPersonalizacion.RETIRADO));
        for (TipoPersonalizacion tipo : TipoPersonalizacion.values()) {
            assertThrows(IllegalArgumentException.class, () -> new PersonalizacionIngrediente(null, fijo, 1, tipo));
        }
        assertThrows(IllegalArgumentException.class, () -> configurar(comida, carne));
        assertThrows(IllegalArgumentException.class, () -> configurar(comida, new Ingrediente(2L, "Carne", 10, UnidadMedida.UNIDAD)));
        assertThrows(UnsupportedOperationException.class, () -> comida.getIngredientes().clear());
    }

    @Test
    void conservaRecetaDelDetalleAnteNuevasConfiguraciones() {
        Comida comida = comida();
        DetallePedido detalle = new DetallePedido(null, comida, 1);
        ConfiguracionIngrediente nuevo = configurar(comida, new Ingrediente(null, "Queso", 0, UnidadMedida.UNIDAD));
        assertTrue(detalle.puedePrepararse());
        assertThrows(IllegalArgumentException.class, () -> detalle.agregarPersonalizacion(
                new PersonalizacionIngrediente(null, nuevo, 1, TipoPersonalizacion.ADICIONAL)));
    }

    @Test
    void pedidoMantieneUnDetalleMinimoYPropiedadExclusiva() {
        DetallePedido detalle = new DetallePedido(null, comida(), 1);
        Pedido pedido = new Pedido(null, cliente(), FECHA, detalle);
        assertThrows(IllegalStateException.class, () -> pedido.eliminarDetalle(detalle));
        assertThrows(IllegalArgumentException.class, () -> pedido.agregarDetalle(detalle));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null, cliente(), FECHA, detalle));
        assertThrows(UnsupportedOperationException.class, () -> pedido.getDetalle().clear());
        assertThrows(UnsupportedOperationException.class, () -> detalle.getDetallePedidoIngrediente().clear());
    }

    @Test
    void stockRechazaNegativosFaltantesYDesbordamientosSinMutacion() {
        Ingrediente ingrediente = new Ingrediente(null, "Queso", 2, UnidadMedida.UNIDAD);
        assertThrows(IllegalArgumentException.class, () -> ingrediente.aumentarStock(0));
        assertThrows(IllegalArgumentException.class, () -> ingrediente.disminuirStock(-1));
        assertThrows(IllegalArgumentException.class, () -> ingrediente.hayStock(-1));
        assertThrows(IllegalStateException.class, () -> ingrediente.disminuirStock(3));
        assertThrows(ArithmeticException.class, () -> ingrediente.aumentarStock(Integer.MAX_VALUE));
        assertEquals(2, ingrediente.getStockActual());
        ingrediente.disminuirStock(2);
        assertTrue(ingrediente.hayStock(0));
        assertEquals(0, ingrediente.getStockActual());
    }

    @Test
    void movimientosAplicanEntradasSalidasYAjustesUnaSolaVez() {
        Ingrediente ingrediente = new Ingrediente(null, "Queso", 2, UnidadMedida.UNIDAD);
        MovimientoInventario entrada = movimiento(ingrediente, 3, TipoMovimiento.ENTRADA);
        assertEquals(2, ingrediente.getStockActual());
        entrada.aplicar();
        assertThrows(IllegalStateException.class, entrada::aplicar);
        assertEquals(5, ingrediente.getStockActual());
        movimiento(ingrediente, 2, TipoMovimiento.SALIDA).aplicar();
        movimiento(ingrediente, -1, TipoMovimiento.AJUSTE).aplicar();
        movimiento(ingrediente, 4, TipoMovimiento.AJUSTE).aplicar();
        assertEquals(6, ingrediente.getStockActual());
        MovimientoInventario fallido = movimiento(ingrediente, 7, TipoMovimiento.SALIDA);
        assertThrows(IllegalStateException.class, fallido::aplicar);
        assertFalse(fallido.isAplicado());
        assertEquals(6, ingrediente.getStockActual());
        assertThrows(IllegalArgumentException.class, () -> movimiento(ingrediente, 0, TipoMovimiento.AJUSTE));
        assertThrows(IllegalArgumentException.class, () -> movimiento(ingrediente, Integer.MIN_VALUE, TipoMovimiento.AJUSTE));
    }

    private MovimientoInventario movimiento(Ingrediente ingrediente, int cantidad, TipoMovimiento tipo) {
        return new MovimientoInventario(null, ingrediente, cantidad, FECHA, "Conteo de prueba", tipo);
    }

    @Test
    void validaDatosObligatoriosCantidadesPreciosYRoles() {
        assertThrows(IllegalArgumentException.class, () -> new Comida(null, " ", BigDecimal.ONE, null));
        assertThrows(IllegalArgumentException.class, () -> new Comida(null, "Pollo", BigDecimal.ONE.negate(), null));
        assertThrows(IllegalArgumentException.class, () -> new DetallePedido(null, comida(), 0));
        assertThrows(IllegalArgumentException.class, () -> new Ingrediente(0L, "Queso", 0, UnidadMedida.UNIDAD));
        assertThrows(IllegalArgumentException.class, () -> new Ingrediente(null, "Queso", -1, UnidadMedida.UNIDAD));
        assertThrows(IllegalArgumentException.class, () -> new Cliente(null, "ana", "clave", "123", "invalido", "Ana", codificador));
        assertEquals(Rol.CLIENTE, cliente().getRol());
        Usuario empleado = new Usuario(null, "empleado", "clave", Rol.EMPLEADO, codificador);
        assertEquals(Rol.EMPLEADO, empleado.getRol());
        assertTrue(empleado.comprobarContraseña("clave"));
        assertFalse(empleado.comprobarContraseña("incorrecta"));
        assertFalse(empleado.comprobarContraseña(null));
    }
}
