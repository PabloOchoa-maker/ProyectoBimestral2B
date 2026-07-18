package streamflow.persistencia;

import java.util.ArrayList;
import java.util.List;
import streamflow.modelo.Usuario;

/**
 * Implementacion en memoria de {@link IUsuarioDao}, util para pruebas
 * unitarias sin depender de la base de datos SQLite.
 */
public class UsuarioDaoMemoria implements IUsuarioDao {

    private final List<Usuario> datos = new ArrayList<>();

    @Override
    public boolean insertar(Usuario u) {
        if (u == null || buscarPorId(u.getId()) != null) {
            return false;
        }
        return datos.add(u);
    }

    @Override
    public List<Usuario> listar() {
        return new ArrayList<>(datos);
    }

    @Override
    public Usuario buscarPorId(String id) {
        for (Usuario u : datos) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public boolean actualizar(Usuario u) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(u.getId())) {
                datos.set(i, u);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(String id) {
        return datos.removeIf(u -> u.getId().equals(id));
    }
}
