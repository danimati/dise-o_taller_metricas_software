package com.gfc.web;

import com.gfc.application.InventarioService;
import com.gfc.web.dto.IngredienteResponse;
import com.gfc.web.dto.MovimientoRequest;
import com.gfc.web.dto.MovimientoResultadoResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Visor de inventario y registro de movimientos manuales; requiere rol EMPLEADO. */
@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {
    private final InventarioService inventarioService;

    public IngredienteController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    /** {@code GET /api/ingredientes} — visor global de inventario (200). */
    @GetMapping
    public List<IngredienteResponse> listar() {
        return inventarioService.listar();
    }

    /** {@code POST /api/ingredientes/{id}/entradas} — carga de inventario (201, 404). */
    @PostMapping("/{id}/entradas")
    public ResponseEntity<MovimientoResultadoResponse> entrada(@PathVariable Long id,
                                                                 @RequestBody MovimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.registrarEntrada(id, request));
    }

    /** {@code POST /api/ingredientes/{id}/salidas} — descarga manual de inventario (201, 404, 409). */
    @PostMapping("/{id}/salidas")
    public ResponseEntity<MovimientoResultadoResponse> salida(@PathVariable Long id,
                                                                @RequestBody MovimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.registrarSalida(id, request));
    }
}
