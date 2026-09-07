package com.gfc.web;

import com.gfc.application.ComidaService;
import com.gfc.web.dto.ComidaDetalleResponse;
import com.gfc.web.dto.ComidaResumenResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Catalogo de comidas, publico y sin paginacion. */
@RestController
@RequestMapping("/api/comidas")
public class ComidaController {
    private final ComidaService comidaService;

    public ComidaController(ComidaService comidaService) {
        this.comidaService = comidaService;
    }

    /** {@code GET /api/comidas} — lista todo el catalogo (200). */
    @GetMapping
    public List<ComidaResumenResponse> listar() {
        return comidaService.listar();
    }

    /** {@code GET /api/comidas/{id}} — detalle con la receta configurable (200, 404). */
    @GetMapping("/{id}")
    public ComidaDetalleResponse obtener(@PathVariable Long id) {
        return comidaService.obtener(id);
    }
}
