package streamflow.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import streamflow.modelo.Calidad;
import streamflow.modelo.Contenido;
import streamflow.modelo.Genero;
import streamflow.modelo.Pelicula;
import streamflow.modelo.Usuario;
import streamflow.persistencia.UsuarioDaoMemoria;

/**
 * Pruebas de {@link UsuarioServiceImpl} con un DAO de usuarios en memoria
 * inyectado por constructor.
 */
class UsuarioServiceImplTest {

    private UsuarioServiceImpl service;

    private Usuario usuarioDe(String id, String nombre) {
        return new Usuario(id, nombre, nombre.toLowerCase() + "@mail.com", null);
    }

    @BeforeEach
    void inicializar() {
        service = new UsuarioServiceImpl(new UsuarioDaoMemoria());
    }

    @Test
    void registrarUsuarioValidoDevuelveTrue() {
        assertTrue(service.registrar(usuarioDe("u1", "Pablo")));
    }

    @Test
    void registrarNullDevuelveFalse() {
        assertFalse(service.registrar(null));
    }

    @Test
    void registrarIdVacioDevuelveFalse() {
        assertFalse(service.registrar(usuarioDe("", "Sin id")));
    }

    @Test
    void registrarIdDuplicadoDevuelveFalse() {
        service.registrar(usuarioDe("u1", "Pablo"));
        assertFalse(service.registrar(usuarioDe("u1", "Pedro")));
    }

    @Test
    void buscarPorIdExistenteDevuelveElUsuario() {
        service.registrar(usuarioDe("u1", "Pablo"));
        assertNotNull(service.buscarPorId("u1"));
        assertEquals("Pablo", service.buscarPorId("u1").getNombre());
    }

    @Test
    void buscarPorIdNuloOvacioDevuelveNull() {
        assertNull(service.buscarPorId(null));
        assertNull(service.buscarPorId(""));
    }

    @Test
    void agregarFavoritoSoloFuncionaConUsuarioYcontenidoValidos() {
        service.registrar(usuarioDe("u1", "Pablo"));
        Contenido peli = new Pelicula("c1", "Interstellar", Genero.DRAMA,
                Calidad.HD, 169, 4.0, "Nolan");

        assertTrue(service.agregarFavorito("u1", peli));
        assertEquals(1, service.buscarPorId("u1").getFavoritos().size());

        assertFalse(service.agregarFavorito("no-existe", peli));
        assertFalse(service.agregarFavorito("u1", null));
        assertFalse(service.agregarFavorito("", peli));
    }
}
