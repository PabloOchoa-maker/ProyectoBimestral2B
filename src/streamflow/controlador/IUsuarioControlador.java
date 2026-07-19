package streamflow.controlador;

import java.util.List;
import streamflow.modelo.Contenido;
import streamflow.modelo.Usuario;

/**
 * Contrato del controlador de usuarios. La vista depende de esta interfaz
 * (no de la clase concreta), cumpliendo la inversion de dependencias.
 */
public interface IUsuarioControlador {

    boolean registrar(Usuario u);

    double facturar(String idUsuario);

    List<Contenido> recomendar(String idUsuario);
}
