package streamflow.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import streamflow.modelo.Usuario;

/**
 * Pruebas del CRUD de {@link UsuarioDaoMemoria}, el DAO en memoria que se usa
 * para probar la logica sin depender de SQLite.
 */
class UsuarioDaoMemoriaTest {

    private UsuarioDaoMemoria dao;

    private Usuario usuarioDe(String id, String nombre) {
        // La suscripcion no interviene en el CRUD del DAO: puede ir en null.
        return new Usuario(id, nombre, nombre.toLowerCase() + "@mail.com", null);
    }

    @BeforeEach
    void inicializar() {
        dao = new UsuarioDaoMemoria();
    }

    @Test
    void insertarYbuscarPorIdDevuelveElUsuario() {
        Usuario u = usuarioDe("u1", "Pablo");
        assertTrue(dao.insertar(u));
        assertSame(u, dao.buscarPorId("u1"));
    }

    @Test
    void insertarNullDevuelveFalse() {
        assertFalse(dao.insertar(null));
        assertTrue(dao.listar().isEmpty());
    }

    @Test
    void insertarIdDuplicadoDevuelveFalse() {
        dao.insertar(usuarioDe("u1", "Pablo"));
        assertFalse(dao.insertar(usuarioDe("u1", "Pedro")));
        assertEquals(1, dao.listar().size());
    }

    @Test
    void buscarPorIdInexistenteDevuelveNull() {
        assertNull(dao.buscarPorId("no-existe"));
    }

    @Test
    void listarDevuelveCopiaIndependiente() {
        dao.insertar(usuarioDe("u1", "Pablo"));
        dao.listar().clear();
        assertEquals(1, dao.listar().size());
    }

    @Test
    void actualizarReemplazaElUsuario() {
        dao.insertar(usuarioDe("u1", "Pablo"));
        Usuario nuevo = usuarioDe("u1", "Pablo Editado");
        assertTrue(dao.actualizar(nuevo));
        assertEquals("Pablo Editado", dao.buscarPorId("u1").getNombre());
    }

    @Test
    void actualizarInexistenteDevuelveFalse() {
        assertFalse(dao.actualizar(usuarioDe("u1", "Pablo")));
    }

    @Test
    void eliminarExistenteLoQuita() {
        dao.insertar(usuarioDe("u1", "Pablo"));
        assertTrue(dao.eliminar("u1"));
        assertNull(dao.buscarPorId("u1"));
    }

    @Test
    void eliminarInexistenteDevuelveFalse() {
        assertFalse(dao.eliminar("no-existe"));
    }
}
