package com.gfc.domain.model;

import jakarta.persistence.*;

import com.gfc.domain.port.CodificadorContrasena;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(com.gfc.infrastructure.security.UsuarioEntityListener.class)
public class Usuario extends BaseEntity {
    protected Usuario() { }

    @Column(nullable = false, unique = true)
    private String nombreUsuario;
    @Column(nullable = false)
    private String contrasena;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
    @Transient
    private CodificadorContrasena codificador;

    public Usuario(Long id, String nombreUsuario, String contrasena, Rol rol,
                   CodificadorContrasena codificador) {
        super(id);
        this.nombreUsuario = Validaciones.texto(nombreUsuario, "nombreUsuario");
        Validaciones.texto(contrasena, "contrasena");
        this.rol = Objects.requireNonNull(rol, "rol");
        this.codificador = Objects.requireNonNull(codificador, "codificador");
        this.contrasena = Validaciones.texto(codificador.codificar(contrasena), "hash");
    }

    public boolean comprobarContraseña(String contraseñaIngresada) {
        return contraseñaIngresada != null && codificador.comprobar(contraseñaIngresada, contrasena);
    }

    public String getNombreUsuario() { return nombreUsuario; }
    /** Restaura el servicio transitorio al cargar la entidad, sin modificar el hash. */
    public void configurarCodificador(CodificadorContrasena codificador) {
        this.codificador = Objects.requireNonNull(codificador, "codificador");
    }
    public Rol getRol() { return rol; }
    public Long getIdUsuario() { return getId(); }
}
