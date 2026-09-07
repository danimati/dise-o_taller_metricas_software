package com.gfc.infrastructure;

import com.gfc.domain.model.*;
import com.gfc.infrastructure.security.Pbkdf2CodificadorContrasena;
import com.gfc.infrastructure.security.UsuarioEntityListener;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({Pbkdf2CodificadorContrasena.class, UsuarioEntityListener.class})
class PersistenciaDominioTest {
    @Autowired EntityManager em;
    @Autowired Pbkdf2CodificadorContrasena codificador;

    @Test
    void guardaYRecuperaPedidoCompletoConRecetaPersonalizacionYCredenciales() {
        Cliente cliente = new Cliente(null, "ana", "clave", "123", "ana@example.com", "Ana", codificador);
        Ingrediente queso = new Ingrediente(null, "Queso", 20, UnidadMedida.UNIDAD);
        Comida comida = new Comida(null, "Hamburguesa", new BigDecimal("20000"), null);
        ConfiguracionIngrediente configuracion = comida.configurarIngrediente(null, queso, 1,
                true, true, true, new BigDecimal("1000"));
        DetallePedido detalle = new DetallePedido(null, comida, 2);
        detalle.agregarPersonalizacion(new PersonalizacionIngrediente(null, configuracion, 3, TipoPersonalizacion.ADICIONAL));
        Pedido pedido = new Pedido(null, cliente, LocalDateTime.now(), detalle);
        em.persist(cliente);
        em.persist(queso);
        em.persist(comida);
        em.persist(pedido);
        em.flush();
        Long pedidoId = pedido.getId();
        assertNotNull(pedidoId);
        em.clear();

        Pedido recuperado = em.find(Pedido.class, pedidoId);
        assertEquals(new BigDecimal("46000.00"), recuperado.calcularTotal());
        assertTrue(recuperado.getCliente().comprobarContraseña("clave"));
        assertFalse(recuperado.getCliente().comprobarContraseña("incorrecta"));
        assertEquals(Rol.CLIENTE, recuperado.getCliente().getRol());
        assertEquals(recuperado.getCliente().getIdUsuario(), recuperado.getCliente().getIdCliente());
        assertInstanceOf(Cliente.class, em.find(Usuario.class, recuperado.getCliente().getId()));
        DetallePedido item = recuperado.getDetalle().getFirst();
        assertEquals(8, item.cantidadesNecesarias().values().iterator().next());
        assertTrue(recuperado.puedePrepararse());

        // La receta del detalle no incorpora configuraciones agregadas despues.
        Ingrediente salsa = new Ingrediente(null, "Salsa", 0, UnidadMedida.UNIDAD);
        em.persist(salsa);
        item.getComida().configurarIngrediente(null, salsa, 1, true, false, false, BigDecimal.ZERO);
        em.flush();
        em.clear();
        recuperado = em.find(Pedido.class, pedidoId);
        assertTrue(recuperado.puedePrepararse());
        item = recuperado.getDetalle().getFirst();
        Long personalizacionId = item.getDetallePedidoIngrediente().getFirst().getId();
        item.eliminarPersonalizacion(item.getDetallePedidoIngrediente().getFirst());
        em.flush();
        em.clear();
        assertNull(em.find(PersonalizacionIngrediente.class, personalizacionId));
        assertEquals(new BigDecimal("40000.00"), em.find(Pedido.class, pedidoId).calcularTotal());
    }

    @Test
    void eliminaDetallesEnCascadaSinEliminarCatalogoNiCliente() {
        Cliente cliente = new Cliente(null, "luis", "clave", "456", "luis@example.com", "Luis", codificador);
        Comida comida = new Comida(null, "Pollo", new BigDecimal("25000"), null);
        DetallePedido primero = new DetallePedido(null, comida, 1);
        DetallePedido segundo = new DetallePedido(null, comida, 2);
        Pedido pedido = new Pedido(null, cliente, LocalDateTime.now(), primero);
        pedido.agregarDetalle(segundo);
        em.persist(cliente);
        em.persist(comida);
        em.persist(pedido);
        em.flush();
        pedido.eliminarDetalle(segundo);
        em.flush();
        Long segundoId = segundo.getId();
        Long primeroId = primero.getId();
        em.remove(pedido);
        em.flush();
        em.clear();
        assertNull(em.find(DetallePedido.class, segundoId));
        assertNull(em.find(DetallePedido.class, primeroId));
        assertNotNull(em.find(Comida.class, comida.getId()));
        assertNotNull(em.find(Cliente.class, cliente.getId()));
    }

    @Test
    void movimientoConservaStockYMarcaDeAplicacionAlRecargar() {
        Ingrediente ingrediente = new Ingrediente(null, "Queso", 10, UnidadMedida.UNIDAD);
        MovimientoInventario movimiento = new MovimientoInventario(null, ingrediente, 3,
                LocalDateTime.now(), "Consumo", TipoMovimiento.SALIDA);
        em.persist(ingrediente);
        em.persist(movimiento);
        movimiento.aplicar();
        em.flush();
        em.clear();
        MovimientoInventario recuperado = em.find(MovimientoInventario.class, movimiento.getId());
        assertTrue(recuperado.isAplicado());
        assertEquals(TipoMovimiento.SALIDA, recuperado.getTipo());
        assertEquals(7, recuperado.getIngrediente().getStockActual());
        assertThrows(IllegalStateException.class, recuperado::aplicar);
    }

    @Test
    void importesCopNoSeRedondeanSilenciosamente() {
        assertEquals(new BigDecimal("1000.00"), new Comida(null, "Papas", new BigDecimal("1000"), null).getPrecio());
        assertThrows(IllegalArgumentException.class,
                () -> new Comida(null, "Papas", new BigDecimal("1000.001"), null));
        assertThrows(IllegalArgumentException.class,
                () -> new Comida(null, "Papas", new BigDecimal("100000000000000000"), null));
    }
}
