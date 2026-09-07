package com.gfc.infrastructure.startup;

import com.gfc.domain.model.Cliente;
import com.gfc.domain.model.Comida;
import com.gfc.domain.model.Ingrediente;
import com.gfc.domain.model.Rol;
import com.gfc.domain.model.TipoDocumento;
import com.gfc.domain.model.UnidadMedida;
import com.gfc.domain.model.Usuario;
import com.gfc.domain.port.CodificadorContrasena;
import com.gfc.infrastructure.config.GfcSeedProperties;
import com.gfc.infrastructure.persistence.ClienteRepository;
import com.gfc.infrastructure.persistence.ComidaRepository;
import com.gfc.infrastructure.persistence.IngredienteRepository;
import com.gfc.infrastructure.persistence.UsuarioRepository;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosDemoSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DatosDemoSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ComidaRepository comidaRepository;
    private final IngredienteRepository ingredienteRepository;
    private final CodificadorContrasena codificador;
    private final GfcSeedProperties props;

    public DatosDemoSeeder(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository,
                            ComidaRepository comidaRepository, IngredienteRepository ingredienteRepository,
                            CodificadorContrasena codificador, GfcSeedProperties props) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.comidaRepository = comidaRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.codificador = codificador;
        this.props = props;
    }

    @Override
    public void run(String... args) {
        sembrarUsuarios();
        sembrarCatalogo();
    }

    private void sembrarUsuarios() {
        if (usuarioRepository.findByNombreUsuario(props.empleado().usuario()).isEmpty()) {
            usuarioRepository.save(new Usuario(null, props.empleado().usuario(), props.empleado().clave(),
                    Rol.EMPLEADO, codificador));
            log.info("Usuario demo creado -> '{}' (EMPLEADO)", props.empleado().usuario());
        }

        if (usuarioRepository.findByNombreUsuario(props.cliente().usuario()).isEmpty()) {
            clienteRepository.save(new Cliente(null, props.cliente().usuario(), props.cliente().clave(),
                    TipoDocumento.CC, props.cliente().documento(), props.cliente().email(),
                    props.cliente().nombre(), codificador));
            log.info("Usuario demo creado -> '{}' (CLIENTE)", props.cliente().usuario());
        }

        if (usuarioRepository.findByNombreUsuario(props.empresa().usuario()).isEmpty()) {
            clienteRepository.save(new Cliente(null, props.empresa().usuario(), props.empresa().clave(),
                    TipoDocumento.NIT, props.empresa().documento(), props.empresa().email(),
                    props.empresa().nombre(), codificador));
            log.info("Usuario demo creado -> '{}' (CLIENTE/EMPRESA)", props.empresa().usuario());
        }
    }

    private void sembrarCatalogo() {
        if (comidaRepository.count() > 0) {
            return;
        }

        Ingrediente queso = ingredienteRepository.save(new Ingrediente(null, "Queso", 100, UnidadMedida.UNIDAD));
        Ingrediente pan = ingredienteRepository.save(new Ingrediente(null, "Pan", 100, UnidadMedida.UNIDAD));
        Ingrediente carneRes = ingredienteRepository.save(new Ingrediente(null, "Carne de res", 5000, UnidadMedida.GRAMO));
        Ingrediente lechuga = ingredienteRepository.save(new Ingrediente(null, "Lechuga", 200, UnidadMedida.UNIDAD));
        Ingrediente tomate = ingredienteRepository.save(new Ingrediente(null, "Tomate", 3000, UnidadMedida.GRAMO));
        Ingrediente salchicha = ingredienteRepository.save(new Ingrediente(null, "Salchicha", 100, UnidadMedida.UNIDAD));
        Ingrediente cebolla = ingredienteRepository.save(new Ingrediente(null, "Cebolla", 2000, UnidadMedida.GRAMO));
        Ingrediente salsaTomate = ingredienteRepository.save(new Ingrediente(null, "Salsa de tomate", 5000, UnidadMedida.MILILITRO));
        Ingrediente papas = ingredienteRepository.save(new Ingrediente(null, "Papas", 5000, UnidadMedida.GRAMO));
        Ingrediente pepperoni = ingredienteRepository.save(new Ingrediente(null, "Pepperoni", 1000, UnidadMedida.GRAMO));
        Ingrediente masa = ingredienteRepository.save(new Ingrediente(null, "Masa", 50, UnidadMedida.UNIDAD));

        Comida hamburguesa = new Comida(null, "Hamburguesa Clasica", new BigDecimal("18000"), null);
        hamburguesa.configurarIngrediente(null, pan, 1, true, true, true, new BigDecimal("2000"));
        hamburguesa.configurarIngrediente(null, carneRes, 200, true, true, true, new BigDecimal("4000"));
        hamburguesa.configurarIngrediente(null, queso, 2, true, true, true, new BigDecimal("1500"));
        hamburguesa.configurarIngrediente(null, lechuga, 1, true, true, true, new BigDecimal("500"));
        hamburguesa.configurarIngrediente(null, tomate, 100, true, true, true, new BigDecimal("500"));
        comidaRepository.save(hamburguesa);

        Comida perroCaliente = new Comida(null, "Perro Caliente", new BigDecimal("12000"), null);
        perroCaliente.configurarIngrediente(null, pan, 1, true, true, true, new BigDecimal("1500"));
        perroCaliente.configurarIngrediente(null, salchicha, 1, true, true, true, new BigDecimal("2000"));
        perroCaliente.configurarIngrediente(null, queso, 1, true, true, true, new BigDecimal("1000"));
        perroCaliente.configurarIngrediente(null, cebolla, 50, true, true, true, new BigDecimal("500"));
        perroCaliente.configurarIngrediente(null, salsaTomate, 30, true, true, true, new BigDecimal("500"));
        comidaRepository.save(perroCaliente);

        Comida pizza = new Comida(null, "Pizza Personal", new BigDecimal("22000"), null);
        pizza.configurarIngrediente(null, masa, 1, true, true, true, new BigDecimal("3000"));
        pizza.configurarIngrediente(null, queso, 3, true, true, true, new BigDecimal("2000"));
        pizza.configurarIngrediente(null, salsaTomate, 50, true, true, true, new BigDecimal("1000"));
        pizza.configurarIngrediente(null, pepperoni, 100, true, true, true, new BigDecimal("3000"));
        pizza.configurarIngrediente(null, cebolla, 50, false, true, false, new BigDecimal("500"));
        comidaRepository.save(pizza);

        Comida papasFrancesa = new Comida(null, "Papas a la Francesa", new BigDecimal("9000"), null);
        papasFrancesa.configurarIngrediente(null, papas, 300, true, true, true, new BigDecimal("2000"));
        papasFrancesa.configurarIngrediente(null, salsaTomate, 40, true, true, true, new BigDecimal("500"));
        comidaRepository.save(papasFrancesa);

        log.info("Catalogo demo creado: 4 comidas, 11 ingredientes");
    }
}
