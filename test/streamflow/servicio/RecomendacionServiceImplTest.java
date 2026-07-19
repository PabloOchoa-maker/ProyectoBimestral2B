package streamflow.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import streamflow.modelo.Calidad;
import streamflow.modelo.Contenido;
import streamflow.modelo.Genero;
import streamflow.modelo.Pelicula;
import streamflow.modelo.Usuario;
import streamflow.persistencia.ContenidoDaoMemoria;

/**
 * Pruebas de {@link RecomendacionServiceImpl#recomendarPorGenero(Usuario)}:
 * recomienda contenidos del mismo genero que los favoritos del usuario,
 * excluyendo los que ya son favoritos.
 */
class RecomendacionServiceImplTest {

    private ContenidoDaoMemoria contenidoDao;
    private RecomendacionServiceImpl service;

    private Contenido pelicula(String id, Genero genero) {
        return new Pelicula(id, "Peli " + id, genero, Calidad.HD, 120, 5.0, "Dir");
    }

    private Usuario usuarioVacio() {
        return new Usuario("u1", "Pablo", "pablo@mail.com", null);
    }

    @BeforeEach
    void inicializar() {
        contenidoDao = new ContenidoDaoMemoria();
        service = new RecomendacionServiceImpl(contenidoDao);
    }

    @Test
    void usuarioNullDevuelveListaVacia() {
        assertTrue(service.recomendarPorGenero(null).isEmpty());
    }

    @Test
    void usuarioSinFavoritosDevuelveListaVacia() {
        assertTrue(service.recomendarPorGenero(usuarioVacio()).isEmpty());
    }

    @Test
    void recomiendaOtroContenidoDelMismoGenero() {
        Contenido favorito = pelicula("c1", Genero.ACCION);
        Contenido candidato = pelicula("c2", Genero.ACCION);
        contenidoDao.insertar(favorito);
        contenidoDao.insertar(candidato);
        Usuario u = usuarioVacio();
        u.agregarFavorito(favorito);

        List<Contenido> recs = service.recomendarPorGenero(u);
        assertEquals(1, recs.size());
        assertEquals("c2", recs.get(0).getId());
    }

    @Test
    void noRecomiendaContenidoDeOtroGenero() {
        Contenido favorito = pelicula("c1", Genero.ACCION);
        Contenido otroGenero = pelicula("c2", Genero.COMEDIA);
        contenidoDao.insertar(favorito);
        contenidoDao.insertar(otroGenero);
        Usuario u = usuarioVacio();
        u.agregarFavorito(favorito);

        assertTrue(service.recomendarPorGenero(u).isEmpty());
    }

    @Test
    void noRecomiendaLosContenidosQueYaSonFavoritos() {
        Contenido favorito = pelicula("c1", Genero.ACCION);
        contenidoDao.insertar(favorito);
        Usuario u = usuarioVacio();
        u.agregarFavorito(favorito);

        List<Contenido> recs = service.recomendarPorGenero(u);
        assertFalse(recs.stream().anyMatch(c -> c.getId().equals("c1")));
    }
}
