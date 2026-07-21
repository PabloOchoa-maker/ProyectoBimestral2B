package streamflow.persistencia;

import java.util.List;
import streamflow.modelo.Contenido;
import streamflow.modelo.Usuario;

public interface IUsuarioDao {

    boolean insertar(Usuario u);

    List<Usuario> listar();

    Usuario buscarPorId(String id);

    boolean actualizar(Usuario u);

    boolean eliminar(String id);

    boolean agregarFavorito(String idUsuario, Contenido c);
}
