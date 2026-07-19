package streamflow.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import streamflow.modelo.Calidad;
import streamflow.modelo.Contenido;
import streamflow.modelo.Genero;
import streamflow.modelo.Pelicula;

/**
 * Pruebas del CRUD de {@link ContenidoDaoMemoria}, el DAO en memoria que se usa
 * para probar la logica sin depender de SQLite.
 */
class ContenidoDaoMemoriaTest {

    private ContenidoDaoMemoria dao;

    private Contenido peliculaDe(String id, String titulo) {
        return new Pelicula(id, titulo, Genero.ACCION, Calidad.HD, 120, 5.0, "Un Director");
    }

    @BeforeEach
    void inicializar() {
        dao = new ContenidoDaoMemoria();
    }

    @Test
    void insertarYbuscarPorIdDevuelveElContenido() {
        Contenido c = peliculaDe("c1", "Matrix");
        assertTrue(dao.insertar(c));
        assertSame(c, dao.buscarPorId("c1"));
    }

    @Test
    void insertarNullDevuelveFalse() {
        assertFalse(dao.insertar(null));
        assertTrue(dao.listar().isEmpty());
    }

    @Test
    void insertarIdDuplicadoDevuelveFalse() {
        dao.insertar(peliculaDe("c1", "Matrix"));
        assertFalse(dao.insertar(peliculaDe("c1", "Otra")));
        assertEquals(1, dao.listar().size());
    }

    @Test
    void buscarPorIdInexistenteDevuelveNull() {
        assertNull(dao.buscarPorId("no-existe"));
    }

    @Test
    void listarDevuelveCopiaIndependiente() {
        dao.insertar(peliculaDe("c1", "Matrix"));
        dao.listar().clear();
        assertEquals(1, dao.listar().size());
    }

    @Test
    void actualizarReemplazaElContenido() {
        dao.insertar(peliculaDe("c1", "Matrix"));
        Contenido nuevo = peliculaDe("c1", "Matrix Reloaded");
        assertTrue(dao.actualizar(nuevo));
        assertEquals("Matrix Reloaded", dao.buscarPorId("c1").getTitulo());
    }

    @Test
    void actualizarInexistenteDevuelveFalse() {
        assertFalse(dao.actualizar(peliculaDe("c1", "Matrix")));
    }

    @Test
    void eliminarExistenteLoQuita() {
        dao.insertar(peliculaDe("c1", "Matrix"));
        assertTrue(dao.eliminar("c1"));
        assertNull(dao.buscarPorId("c1"));
    }

    @Test
    void eliminarInexistenteDevuelveFalse() {
        assertFalse(dao.eliminar("no-existe"));
    }
}
