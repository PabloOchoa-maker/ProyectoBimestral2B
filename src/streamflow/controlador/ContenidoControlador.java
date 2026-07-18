package streamflow.controlador;

import java.util.List;
import streamflow.modelo.Contenido;
import streamflow.servicio.IContenidoService;

/**
 * Controlador de la capa de presentacion para las operaciones de contenido.
 * Solo conoce la interfaz {@link IContenidoService}, nunca la implementacion
 * concreta ni los DAOs.
 */
public class ContenidoControlador {

    private final IContenidoService contenidoService;

    public ContenidoControlador(IContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    public boolean registrar(Contenido c) {
        return contenidoService.registrar(c);
    }

    public List<Contenido> listar() {
        return contenidoService.listar();
    }
}
