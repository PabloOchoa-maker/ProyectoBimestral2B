package streamflow.servicio;

import streamflow.modelo.Contenido;
import streamflow.modelo.Usuario;
import streamflow.persistencia.IContenidoDao;

/**
 * Calcula el costo mensual de un usuario a partir de su suscripcion base mas
 * el costo de los contenidos que tiene marcados como favoritos.
 */
public class SuscripcionServiceImpl implements ISuscripcionService {

    private final IContenidoDao contenidoDao;

    public SuscripcionServiceImpl(IContenidoDao contenidoDao) {
        this.contenidoDao = contenidoDao;
    }

    @Override
    public double calcularCostoMensual(Usuario u) {
        if (u == null) {
            return 0.0;
        }

        double total = 0.0;
        if (u.getSuscripcion() != null) {
            total += u.getSuscripcion().getCostoMensual();
        }

        for (Contenido favorito : u.getFavoritos()) {
            // Se recalcula desde la fuente de datos para respetar cambios de precio.
            Contenido actual = contenidoDao.buscarPorId(favorito.getId());
            Contenido referencia = actual != null ? actual : favorito;
            total += referencia.calcularCosto();
        }
        return total;
    }
}
