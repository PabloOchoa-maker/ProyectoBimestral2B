package streamflow.servicio;

import java.util.ArrayList;
import java.util.List;
import streamflow.modelo.Contenido;
import streamflow.modelo.Genero;
import streamflow.modelo.Usuario;
import streamflow.persistencia.IContenidoDao;

/**
 * Recomienda contenidos segun los generos presentes en los favoritos del
 * usuario, excluyendo los que ya tiene marcados como favoritos.
 */
public class RecomendacionServiceImpl implements IRecomendacionService {

    private final IContenidoDao contenidoDao;

    public RecomendacionServiceImpl(IContenidoDao contenidoDao) {
        this.contenidoDao = contenidoDao;
    }

    @Override
    public List<Contenido> recomendarPorGenero(Usuario u) {
        List<Contenido> recomendados = new ArrayList<>();
        if (u == null || u.getFavoritos().isEmpty()) {
            return recomendados;
        }

        List<Genero> generosPreferidos = new ArrayList<>();
        List<String> idsFavoritos = new ArrayList<>();
        for (Contenido favorito : u.getFavoritos()) {
            idsFavoritos.add(favorito.getId());
            if (!generosPreferidos.contains(favorito.getGenero())) {
                generosPreferidos.add(favorito.getGenero());
            }
        }

        for (Contenido c : contenidoDao.listar()) {
            if (generosPreferidos.contains(c.getGenero()) && !idsFavoritos.contains(c.getId())) {
                recomendados.add(c);
            }
        }
        return recomendados;
    }
}
