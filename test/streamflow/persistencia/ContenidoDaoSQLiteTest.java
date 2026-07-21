package streamflow.persistencia;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import streamflow.modelo.Calidad;
import streamflow.modelo.Contenido;
import streamflow.modelo.Documental;
import streamflow.modelo.Genero;
import streamflow.modelo.Pelicula;
import streamflow.modelo.Podcast;
import streamflow.modelo.Serie;

/**
 * Pruebas del CRUD de {@link ContenidoDaoSQLite} contra la base de datos real.
 *
 * A diferencia de {@link ContenidoDaoMemoriaTest}, aqui si se abre una conexion
 * JDBC: lo que se valida es que las sentencias SQL y el mapeo entre objetos y
 * filas funcionen de verdad.
 *
 * Para no ensuciar los datos de la aplicacion, todos los registros que crea la
 * prueba usan identificadores con el prefijo {@code TEST-} y se borran en el
 * metodo anotado con {@code @AfterEach}, pase o falle el test.
 */
class ContenidoDaoSQLiteTest {

    /** Prefijo de los ids creados por esta prueba, para poder limpiarlos. */
    private static final String PREFIJO = "TEST-";

    private ContenidoDaoSQLite dao;

    private Contenido peliculaDe(String id, String titulo) {
        return new Pelicula(PREFIJO + id, titulo, Genero.ACCION, Calidad.HD, 120, 5.0, "Un Director");
    }

    @BeforeEach
    void inicializar() {
        // El constructor crea las tablas si aun no existen.
        dao = new ContenidoDaoSQLite();
        limpiar();
    }

    @AfterEach
    void limpiar() {
        for (Contenido c : dao.listar()) {
            if (c.getId().startsWith(PREFIJO)) {
                dao.eliminar(c.getId());
            }
        }
    }

    @Test
    void insertarGuardaElContenidoEnLaBase() {
        assertTrue(dao.insertar(peliculaDe("c1", "Matrix")));

        Contenido leido = dao.buscarPorId(PREFIJO + "c1");
        assertNotNull(leido, "el contenido insertado deberia poder leerse de la base");
        assertEquals("Matrix", leido.getTitulo());
    }

    @Test
    void elContenidoLeidoConservaTodosSusDatos() {
        dao.insertar(new Serie(PREFIJO + "c2", "Breaking Bad", Genero.DRAMA,
                Calidad.UHD_4K, 47, 2.0, 5, 62));

        Contenido leido = dao.buscarPorId(PREFIJO + "c2");
        assertEquals("Breaking Bad", leido.getTitulo());
        assertEquals(Genero.DRAMA, leido.getGenero());
        assertEquals(Calidad.UHD_4K, leido.getCalidad());
        assertEquals(47, leido.getDuracionMin());
        assertEquals(2.0, leido.getCostoBase(), 0.0001);
    }

    /**
     * La tabla es plana pero la jerarquia no: la columna 'tipo' es la que
     * permite reconstruir la subclase correcta al leer. Sin ella se perderia
     * el polimorfismo al recuperar los datos.
     */
    @Test
    void elTipoConcretoSeConservaAlLeerDeLaBase() {
        dao.insertar(new Pelicula(PREFIJO + "p", "Interstellar", Genero.DRAMA,
                Calidad.UHD_4K, 169, 4.0, "Christopher Nolan"));
        dao.insertar(new Serie(PREFIJO + "s", "The Office", Genero.COMEDIA,
                Calidad.HD, 22, 2.0, 9, 201));
        dao.insertar(new Documental(PREFIJO + "d", "Planet Earth", Genero.DOCUMENTAL,
                Calidad.UHD_4K, 50, 3.0, "Naturaleza"));
        dao.insertar(new Podcast(PREFIJO + "o", "Deep Questions", Genero.COMEDIA,
                Calidad.SD, 60, 1.0, "Cal Newport"));

        assertInstanceOf(Pelicula.class, dao.buscarPorId(PREFIJO + "p"));
        assertInstanceOf(Serie.class, dao.buscarPorId(PREFIJO + "s"));
        assertInstanceOf(Documental.class, dao.buscarPorId(PREFIJO + "d"));
        assertInstanceOf(Podcast.class, dao.buscarPorId(PREFIJO + "o"));
    }

    /** Los atributos propios de cada subclase viajan en las columnas extra. */
    @Test
    void losAtributosPropiosDeCadaSubclaseSePersisten() {
        dao.insertar(new Pelicula(PREFIJO + "p", "Interstellar", Genero.DRAMA,
                Calidad.UHD_4K, 169, 4.0, "Christopher Nolan"));
        dao.insertar(new Serie(PREFIJO + "s", "The Office", Genero.COMEDIA,
                Calidad.HD, 22, 2.0, 9, 201));

        Pelicula pelicula = (Pelicula) dao.buscarPorId(PREFIJO + "p");
        assertEquals("Christopher Nolan", pelicula.getDirector());

        Serie serie = (Serie) dao.buscarPorId(PREFIJO + "s");
        assertEquals(9, serie.getTemporadas());
        assertEquals(201, serie.getEpisodios());
    }

    @Test
    void listarIncluyeLosContenidosInsertados() {
        dao.insertar(peliculaDe("c1", "Matrix"));
        dao.insertar(peliculaDe("c2", "Origen"));

        long propios = dao.listar().stream()
                .filter(c -> c.getId().startsWith(PREFIJO))
                .count();
        assertEquals(2, propios);
    }

    @Test
    void buscarPorIdInexistenteDevuelveNull() {
        assertNull(dao.buscarPorId(PREFIJO + "no-existe"));
    }

    @Test
    void insertarIdDuplicadoDevuelveFalse() {
        assertTrue(dao.insertar(peliculaDe("c1", "Matrix")));
        // La clave primaria de la tabla impide el duplicado.
        assertFalse(dao.insertar(peliculaDe("c1", "Otra")));
    }

    @Test
    void actualizarCambiaLosDatosEnLaBase() {
        dao.insertar(peliculaDe("c1", "Matrix"));

        assertTrue(dao.actualizar(peliculaDe("c1", "Matrix Reloaded")));
        assertEquals("Matrix Reloaded", dao.buscarPorId(PREFIJO + "c1").getTitulo());
    }

    @Test
    void actualizarInexistenteDevuelveFalse() {
        assertFalse(dao.actualizar(peliculaDe("no-existe", "Matrix")));
    }

    @Test
    void eliminarQuitaElContenidoDeLaBase() {
        dao.insertar(peliculaDe("c1", "Matrix"));

        assertTrue(dao.eliminar(PREFIJO + "c1"));
        assertNull(dao.buscarPorId(PREFIJO + "c1"));
    }

    @Test
    void eliminarInexistenteDevuelveFalse() {
        assertFalse(dao.eliminar(PREFIJO + "no-existe"));
    }

    /**
     * Los datos siguen en la base despues de cerrar el DAO: cada operacion abre
     * y cierra su propia conexion, asi que una instancia nueva ve lo que grabo
     * la anterior. Esto es lo que hace que la informacion sobreviva al cierre
     * de la aplicacion.
     */
    @Test
    void losDatosPersistenParaUnaInstanciaNuevaDelDao() {
        dao.insertar(peliculaDe("c1", "Matrix"));

        ContenidoDaoSQLite otroDao = new ContenidoDaoSQLite();
        assertNotNull(otroDao.buscarPorId(PREFIJO + "c1"));
    }
}
