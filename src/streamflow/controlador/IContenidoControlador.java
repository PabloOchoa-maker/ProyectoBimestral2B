package streamflow.controlador;

import java.util.List;
import streamflow.modelo.Contenido;

/**
 * Contrato del controlador de contenidos. La vista depende de esta interfaz
 * (no de la clase concreta), cumpliendo la inversion de dependencias.
 */
public interface IContenidoControlador {

    boolean registrar(Contenido c);

    List<Contenido> listar();
}
