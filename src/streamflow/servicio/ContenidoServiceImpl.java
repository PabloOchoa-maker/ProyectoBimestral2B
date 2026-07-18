package streamflow.servicio;

import java.util.List;
import streamflow.modelo.Contenido;
import streamflow.persistencia.IContenidoDao;

/**
 * Logica de negocio de contenidos. Delega la persistencia en un
 * {@link IContenidoDao} recibido por constructor (inyeccion de dependencias).
 */
public class ContenidoServiceImpl implements IContenidoService {

    private final IContenidoDao contenidoDao;

    public ContenidoServiceImpl(IContenidoDao contenidoDao) {
        this.contenidoDao = contenidoDao;
    }

    @Override
    public boolean registrar(Contenido c) {
        if (c == null || c.getId() == null || c.getId().isEmpty()) {
            return false;
        }
        if (contenidoDao.buscarPorId(c.getId()) != null) {
            return false;
        }
        return contenidoDao.insertar(c);
    }

    @Override
    public List<Contenido> listar() {
        return contenidoDao.listar();
    }
}
