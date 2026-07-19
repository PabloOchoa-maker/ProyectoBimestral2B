package streamflow.vista;

import java.util.List;
import streamflow.controlador.ContenidoControlador;
import streamflow.controlador.UsuarioControlador;
import streamflow.modelo.Contenido;

public interface IVista {

    void mostrarMenu();

    void mostrarContenidos(List<Contenido> lista);

    /**
     * Arranca el bucle de interaccion con el usuario. Toda la lectura de
     * teclado (Scanner) y el menu viven en la vista; Main solo la invoca.
     *
     * @param contenidoControlador controlador para operaciones de contenido
     * @param usuarioControlador    controlador para operaciones de usuario
     */
    void iniciar(ContenidoControlador contenidoControlador,
                 UsuarioControlador usuarioControlador);
}
