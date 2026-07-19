package streamflow.controlador;

import java.util.List;
import streamflow.modelo.Contenido;
import streamflow.modelo.Usuario;
import streamflow.servicio.IRecomendacionService;
import streamflow.servicio.ISuscripcionService;
import streamflow.servicio.IUsuarioService;

/**
 * Controlador de usuarios. Orquesta los servicios de usuario, suscripcion y
 * recomendacion sin acoplarse a sus implementaciones concretas.
 */
public class UsuarioControlador {

    private final IUsuarioService usuarioService;
    private final ISuscripcionService suscripcionService;
    private final IRecomendacionService recomendacionService;

    public UsuarioControlador(IUsuarioService usuarioService,
                              ISuscripcionService suscripcionService,
                              IRecomendacionService recomendacionService) {
        this.usuarioService = usuarioService;
        this.suscripcionService = suscripcionService;
        this.recomendacionService = recomendacionService;
    }

    public boolean registrar(Usuario u) {
        return usuarioService.registrar(u);
    }

    public double facturar(String idUsuario) {
        Usuario u = usuarioService.buscarPorId(idUsuario);
        if (u == null) {
            return 0.0;
        }
        return suscripcionService.calcularCostoMensual(u);
    }

    public List<Contenido> recomendar(String idUsuario) {
        Usuario u = usuarioService.buscarPorId(idUsuario);
        if (u == null) {
            return java.util.Collections.emptyList();
        }
        return recomendacionService.recomendarPorGenero(u);
    }
}
