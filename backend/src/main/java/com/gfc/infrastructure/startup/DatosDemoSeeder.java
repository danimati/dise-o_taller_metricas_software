package com.gfc.infrastructure.startup;

import com.gfc.domain.model.Comida;
import com.gfc.domain.model.Ingrediente;
import com.gfc.domain.model.Rol;
import com.gfc.domain.model.UnidadMedida;
import com.gfc.domain.model.Usuario;
import com.gfc.domain.port.CodificadorContrasena;
import com.gfc.infrastructure.persistence.ComidaRepository;
import com.gfc.infrastructure.persistence.IngredienteRepository;
import com.gfc.infrastructure.persistence.UsuarioRepository;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Siembra, solo si no existen, un empleado y un catalogo minimo para poder probar
 * de punta a punta (login, ver comidas, crear pedido) sin cargar datos a mano. */
@Component
public class DatosDemoSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DatosDemoSeeder.class);
    private static final String USUARIO_EMPLEADO = "empleado";
    private static final String CLAVE_EMPLEADO = "empleado123";

    private final UsuarioRepository usuarioRepository;
    private final ComidaRepository comidaRepository;
    private final IngredienteRepository ingredienteRepository;
    private final CodificadorContrasena codificador;

    public DatosDemoSeeder(UsuarioRepository usuarioRepository, ComidaRepository comidaRepository,
                            IngredienteRepository ingredienteRepository, CodificadorContrasena codificador) {
        this.usuarioRepository = usuarioRepository;
        this.comidaRepository = comidaRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.codificador = codificador;
    }

    @Override
    public void run(String... args) {
        sembrarEmpleado();
        sembrarCatalogo();
    }

    private void sembrarEmpleado() {
        if (usuarioRepository.findByNombreUsuario(USUARIO_EMPLEADO).isPresent()) {
            return;
        }
        usuarioRepository.save(new Usuario(null, USUARIO_EMPLEADO, CLAVE_EMPLEADO, Rol.EMPLEADO, codificador));
        log.info("Empleado demo creado -> usuario: '{}' / clave: '{}'", USUARIO_EMPLEADO, CLAVE_EMPLEADO);
    }

    private void sembrarCatalogo() {
        if (comidaRepository.count() > 0) {
            return;
        }
        Ingrediente queso = ingredienteRepository.save(new Ingrediente(null, "Queso", 50, UnidadMedida.UNIDAD));
        Comida hamburguesa = new Comida(null, "Hamburguesa Clasica", new BigDecimal("18000"), null);
        hamburguesa.configurarIngrediente(null, queso, 1, true, true, true, new BigDecimal("2000"));
        comidaRepository.save(hamburguesa);
        log.info("Catalogo demo creado: 1 comida ('Hamburguesa Clasica') con 1 ingrediente ('Queso')");
    }
}
