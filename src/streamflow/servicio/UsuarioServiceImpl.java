package streamflow.servicio;

import streamflow.modelo.Contenido;
import streamflow.modelo.Usuario;
import streamflow.persistencia.IUsuarioDao;

/**
 * Logica de negocio de usuarios. Delega la persistencia en un
 * {@link IUsuarioDao} recibido por constructor.
 */
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioDao usuarioDao;

    public UsuarioServiceImpl(IUsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public boolean registrar(Usuario u) {
        if (u == null || u.getId() == null || u.getId().isEmpty()) {
            return false;
        }
        if (usuarioDao.buscarPorId(u.getId()) != null) {
            return false;
        }
        return usuarioDao.insertar(u);
    }

    @Override
    public Usuario buscarPorId(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return usuarioDao.buscarPorId(id);
    }

    @Override
    public boolean agregarFavorito(String idUsuario, Contenido c) {
        if (idUsuario == null || idUsuario.isEmpty() || c == null) {
            return false;
        }
        if (usuarioDao.buscarPorId(idUsuario) == null) {
            return false;
        }
        return usuarioDao.agregarFavorito(idUsuario, c);
    }
}
