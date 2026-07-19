package streamflow.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import streamflow.modelo.Calidad;
import streamflow.modelo.Contenido;
import streamflow.modelo.Genero;
import streamflow.modelo.Pelicula;
import streamflow.persistencia.ContenidoDaoMemoria;

/**
 * Pruebas de {@link ContenidoServiceImpl} usando un DAO en memoria inyectado,
 * de modo que la logica de negocio se prueba sin tocar SQLite.
 */
class ContenidoServiceImplTest {

    private ContenidoServiceImpl service;

    private Contenido peliculaDe(String id, String titulo) {
        return new Pelicula(id, titulo, Genero.ACCION, Calidad.HD, 120, 5.0, "Un Director");
    }

    @BeforeEach
    void inicializar() {
        service = new ContenidoServiceImpl(new ContenidoDaoMemoria());
    }

    @Test
    void registrarContenidoValidoDevuelveTrue() {
        assertTrue(service.registrar(peliculaDe("c1", "Matrix")));
        assertEquals(1, service.listar().size());
    }

    @Test
    void registrarNullDevuelveFalse() {
        assertFalse(service.registrar(null));
    }

    @Test
    void registrarIdVacioDevuelveFalse() {
        assertFalse(service.registrar(peliculaDe("", "Sin id")));
    }

    @Test
    void registrarIdDuplicadoDevuelveFalse() {
        service.registrar(peliculaDe("c1", "Matrix"));
        assertFalse(service.registrar(peliculaDe("c1", "Otra")));
        assertEquals(1, service.listar().size());
    }

    @Test
    void listarSinRegistrosDevuelveListaVacia() {
        assertTrue(service.listar().isEmpty());
    }
}
