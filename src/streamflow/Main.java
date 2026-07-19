package streamflow;

import java.time.LocalDate;
import streamflow.controlador.ContenidoControlador;
import streamflow.controlador.UsuarioControlador;
import streamflow.modelo.Calidad;
import streamflow.modelo.Contenido;
import streamflow.modelo.Documental;
import streamflow.modelo.Genero;
import streamflow.modelo.Pelicula;
import streamflow.modelo.Podcast;
import streamflow.modelo.Serie;
import streamflow.modelo.Suscripcion;
import streamflow.modelo.Usuario;
import streamflow.persistencia.ContenidoDaoSQLite;
import streamflow.persistencia.IContenidoDao;
import streamflow.persistencia.IUsuarioDao;
import streamflow.persistencia.UsuarioDaoSQLite;
import streamflow.servicio.ContenidoServiceImpl;
import streamflow.servicio.IContenidoService;
import streamflow.servicio.IRecomendacionService;
import streamflow.servicio.ISuscripcionService;
import streamflow.servicio.IUsuarioService;
import streamflow.servicio.RecomendacionServiceImpl;
import streamflow.servicio.SuscripcionServiceImpl;
import streamflow.servicio.UsuarioServiceImpl;
import streamflow.vista.ConsolaVista;
import streamflow.vista.IVista;

/**
 * Punto de entrada de StreamFlow. Cablea todas las capas (composicion raiz):
 * DAOs -> servicios -> controladores -> vista, y ejecuta un menu de consola.
 *
 * @author pablo
 */
public class Main {

    public static void main(String[] args) {
        // 1. Persistencia (implementacion concreta elegida aqui).
        IContenidoDao contenidoDao = new ContenidoDaoSQLite();
        IUsuarioDao usuarioDao = new UsuarioDaoSQLite();

        // 2. Servicios (dependen de las interfaces DAO).
        IContenidoService contenidoService = new ContenidoServiceImpl(contenidoDao);
        IUsuarioService usuarioService = new UsuarioServiceImpl(usuarioDao);
        ISuscripcionService suscripcionService = new SuscripcionServiceImpl(contenidoDao);
        IRecomendacionService recomendacionService = new RecomendacionServiceImpl(contenidoDao);

        // 3. Controladores (dependen de las interfaces de servicio).
        ContenidoControlador contenidoControlador = new ContenidoControlador(contenidoService);
        UsuarioControlador usuarioControlador = new UsuarioControlador(
                usuarioService, suscripcionService, recomendacionService);

        // 4. Vista (se programa contra la interfaz IVista).
        IVista vista = new ConsolaVista();

        // 5. Datos de ejemplo (solo la primera ejecucion, si la BD esta vacia).
        sembrarDatos(contenidoControlador, usuarioControlador);

        // 6. La vista arranca el bucle de interaccion (Scanner y menu viven en ella).
        vista.iniciar(contenidoControlador, usuarioControlador);
    }

    private static void sembrarDatos(ContenidoControlador contenidoControlador,
                                     UsuarioControlador usuarioControlador) {
        if (!contenidoControlador.listar().isEmpty()) {
            return;
        }
        contenidoControlador.registrar(new Pelicula(
                "C1", "Interstellar", Genero.DRAMA, Calidad.UHD_4K, 169, 4.0, "Christopher Nolan"));
        contenidoControlador.registrar(new Serie(
                "C2", "Breaking Bad", Genero.DRAMA, Calidad.HD, 47, 2.0, 5, 62));
        contenidoControlador.registrar(new Documental(
                "C3", "Planet Earth", Genero.DOCUMENTAL, Calidad.UHD_4K, 50, 3.0, "Naturaleza"));
        contenidoControlador.registrar(new Podcast(
                "C4", "Deep Questions", Genero.COMEDIA, Calidad.SD, 60, 1.0, "Cal Newport"));

        Usuario usuario = new Usuario("U1", "Pablo Ochoa", "pablo@utpl.edu.ec",
                new Suscripcion("S1", LocalDate.now(), 5.0));
        Contenido favorito = contenidoControlador.listar().get(0);
        usuario.agregarFavorito(favorito);
        usuarioControlador.registrar(usuario);
    }
}
