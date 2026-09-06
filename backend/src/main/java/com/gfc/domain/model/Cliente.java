package com.gfc.domain.model;

import jakarta.persistence.*;

import com.gfc.domain.port.CodificadorContrasena;
import java.util.regex.Pattern;

@Entity
@Table(name = "clientes")
public class Cliente extends Usuario {
    protected Cliente() { }

    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    @Column(nullable = false, unique = true)
    private String documento;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String nombre;

    public Cliente(Long id, String nombreUsuario, String contrasena, String documento,
                   String email, String nombre, CodificadorContrasena codificador) {
        super(id, nombreUsuario, contrasena, Rol.CLIENTE, codificador);
        this.documento = Validaciones.texto(documento, "documento");
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.email = Validaciones.texto(email, "email");
        if (!EMAIL.matcher(this.email).matches()) {
            throw new IllegalArgumentException("Email invalido");
        }
    }

    public String getDocumento() { return documento; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public Long getIdCliente() { return getId(); }
}
