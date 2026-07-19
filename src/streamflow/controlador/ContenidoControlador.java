package streamflow.controlador;

import java.util.List;
import streamflow.modelo.Contenido;
import streamflow.servicio.IContenidoService;

/**
 * Controlador de la capa de presentacion para las operaciones de contenido.
 * Solo conoce la interfaz {@link IContenidoService}, nunca la implementacion
 * concreta ni los DAOs.
 */
public class ContenidoControlador implements IContenidoControlador {

    private final IContenidoService contenidoService;

    public ContenidoControlador(IContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    @Override
    public boolean registrar(Contenido c) {
        return contenidoService.registrar(c);
    }

    @Override
    public List<Contenido> listar() {
        return contenidoService.listar();
    }
}
