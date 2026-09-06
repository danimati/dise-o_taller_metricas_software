package com.gfc.infrastructure.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Pbkdf2CodificadorContrasenaTest {
    @Test
    void generaHashesConSaltYCompruebaSinAlterarLaContrasena() {
        var codificador = new Pbkdf2CodificadorContrasena();
        String clave = " clave-con-espacios-ñ ";
        String hash = codificador.codificar(clave);
        assertNotEquals(clave, hash);
        assertNotEquals(hash, codificador.codificar(clave));
        assertTrue(codificador.comprobar(clave, hash));
        assertFalse(codificador.comprobar(clave.strip(), hash));
        assertFalse(codificador.comprobar(null, hash));
        assertFalse(codificador.comprobar(clave, "hash-invalido"));
        assertFalse(codificador.comprobar(clave, "pbkdf2-sha256$600000$!!!$!!!"));
        assertFalse(codificador.comprobar(clave, "pbkdf2-sha256$999999999$AA==$AA=="));
        assertThrows(IllegalArgumentException.class, () -> codificador.codificar(" "));
    }
}
