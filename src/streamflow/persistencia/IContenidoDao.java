package streamflow.persistencia;

import java.util.List;
import streamflow.modelo.Contenido;

public interface IContenidoDao {

    boolean insertar(Contenido c);

    List<Contenido> listar();

    Contenido buscarPorId(String id);

    boolean actualizar(Contenido c);

    boolean eliminar(String id);
}
