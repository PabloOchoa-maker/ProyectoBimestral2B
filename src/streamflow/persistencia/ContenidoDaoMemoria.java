package streamflow.persistencia;

import java.util.ArrayList;
import java.util.List;
import streamflow.modelo.Contenido;

/**
 * Implementacion en memoria de {@link IContenidoDao}, util para pruebas
 * unitarias sin depender de la base de datos SQLite.
 */
public class ContenidoDaoMemoria implements IContenidoDao {

    private final List<Contenido> datos = new ArrayList<>();

    @Override
    public boolean insertar(Contenido c) {
        if (c == null || buscarPorId(c.getId()) != null) {
            return false;
        }
        return datos.add(c);
    }

    @Override
    public List<Contenido> listar() {
        return new ArrayList<>(datos);
    }

    @Override
    public Contenido buscarPorId(String id) {
        for (Contenido c : datos) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public boolean actualizar(Contenido c) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(c.getId())) {
                datos.set(i, c);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(String id) {
        return datos.removeIf(c -> c.getId().equals(id));
    }
}
