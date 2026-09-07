package com.gfc.application;

import com.gfc.application.exception.RecursoNoEncontradoException;
import com.gfc.domain.model.Comida;
import com.gfc.domain.model.ConfiguracionIngrediente;
import com.gfc.infrastructure.persistence.ComidaRepository;
import com.gfc.web.dto.ComidaDetalleResponse;
import com.gfc.web.dto.ComidaResumenResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta de solo lectura del catalogo publico de comidas. */
@Service
public class ComidaService {
    private final ComidaRepository comidaRepository;

    public ComidaService(ComidaRepository comidaRepository) {
        this.comidaRepository = comidaRepository;
    }

    /** @return todas las comidas del catalogo, sin paginacion. */
    @Transactional(readOnly = true)
    public List<ComidaResumenResponse> listar() {
        return comidaRepository.findAll().stream()
                .map(c -> new ComidaResumenResponse(c.getId(), c.getNombre(), c.getPrecio(), c.getImagenURL()))
                .toList();
    }

    /** @throws RecursoNoEncontradoException si no existe una comida con ese id. */
    @Transactional(readOnly = true)
    public ComidaDetalleResponse obtener(Long id) {
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La comida " + id + " no existe"));
        List<ComidaDetalleResponse.ConfiguracionResponse> ingredientes = comida.getIngredientes().stream()
                .map(this::mapearConfiguracion)
                .toList();
        return new ComidaDetalleResponse(comida.getId(), comida.getNombre(), comida.getPrecio(),
                comida.getImagenURL(), ingredientes);
    }

    private ComidaDetalleResponse.ConfiguracionResponse mapearConfiguracion(ConfiguracionIngrediente c) {
        var ingrediente = new ComidaDetalleResponse.IngredienteResumen(c.getIngrediente().getId(),
                c.getIngrediente().getNombreIngrediente(), c.getIngrediente().getUnidadMedida().name());
        return new ComidaDetalleResponse.ConfiguracionResponse(c.getId(), ingrediente, c.getCantidadBase(),
                c.isIncluidoPorDefecto(), c.isPermiteAdicional(), c.isPermiteRetirar(), c.getPrecioAdicional());
    }
}
