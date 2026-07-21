package streamflow.modelo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del comportamiento polimorfico de la jerarquia {@link Contenido}.
 *
 * Lo que se valida aqui no es una regla de negocio, sino el diseno: que cada
 * subclase responda lo suyo a {@code reproducir()} y {@code obtenerDetalles()}
 * cuando se la trata como un {@code Contenido} generico, sin preguntar de que
 * tipo es.
 */
class ContenidoPolimorfismoTest {

    private List<Contenido> catalogo;

    private final PrintStream salidaOriginal = System.out;
    private ByteArrayOutputStream salidaCapturada;

    @BeforeEach
    void inicializar() {
        catalogo = new ArrayList<>();
        catalogo.add(new Pelicula("c1", "Interstellar", Genero.DRAMA,
                Calidad.UHD_4K, 169, 4.0, "Christopher Nolan"));
        catalogo.add(new Serie("c2", "Breaking Bad", Genero.DRAMA,
                Calidad.HD, 47, 2.0, 5, 62));
        catalogo.add(new Documental("c3", "Planet Earth", Genero.DOCUMENTAL,
                Calidad.UHD_4K, 50, 3.0, "Naturaleza"));
        catalogo.add(new Podcast("c4", "Deep Questions", Genero.COMEDIA,
                Calidad.SD, 60, 1.0, "Cal Newport"));

        salidaCapturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salidaCapturada));
    }

    @AfterEach
    void restaurarSalida() {
        System.setOut(salidaOriginal);
    }

    /**
     * El caso que pide el enunciado: recorrer una lista de favoritos tratando a
     * todos como {@code Contenido}. Ninguna subclase rompe el recorrido.
     */
    @Test
    void todasLasSubclasesSeProcesanComoContenido() {
        for (Contenido c : catalogo) {
            assertNotNull(c.obtenerDetalles());
            assertFalse(c.obtenerDetalles().isBlank());
            assertDoesNotThrow(c::reproducir);
        }
    }

    /** Cada subclase se identifica a si misma: la llamada es la misma, la respuesta no. */
    @Test
    void cadaSubclaseDevuelveSusPropiosDetalles() {
        assertTrue(catalogo.get(0).obtenerDetalles().startsWith("Pelicula:"));
        assertTrue(catalogo.get(1).obtenerDetalles().startsWith("Serie:"));
        assertTrue(catalogo.get(2).obtenerDetalles().startsWith("Documental:"));
        assertTrue(catalogo.get(3).obtenerDetalles().startsWith("Podcast:"));
    }

    /** Los detalles incluyen el atributo propio de cada tipo. */
    @Test
    void losDetallesIncluyenElAtributoEspecificoDeCadaTipo() {
        assertTrue(catalogo.get(0).obtenerDetalles().contains("Christopher Nolan"));
        assertTrue(catalogo.get(1).obtenerDetalles().contains("Temporadas: 5"));
        assertTrue(catalogo.get(2).obtenerDetalles().contains("Naturaleza"));
        assertTrue(catalogo.get(3).obtenerDetalles().contains("Cal Newport"));
    }

    /** Misma llamada, cuatro mensajes distintos: despacho dinamico. */
    @Test
    void reproducirImprimeUnMensajeDistintoSegunElTipo() {
        for (Contenido c : catalogo) {
            c.reproducir();
        }
        String salida = salidaCapturada.toString();

        assertTrue(salida.contains("Reproduciendo la pelicula: Interstellar"));
        assertTrue(salida.contains("Reproduciendo la serie: Breaking Bad"));
        assertTrue(salida.contains("Reproduciendo el documental: Planet Earth"));
        assertTrue(salida.contains("Reproduciendo el podcast: Deep Questions"));
    }

    /**
     * {@code calcularCosto()} no se sobrescribe: vive una sola vez en la clase
     * padre y todas las subclases lo heredan igual. Lo que cambia el resultado
     * es el estado (costo base y calidad), no el tipo.
     */
    @Test
    void calcularCostoEsHeredadoYseComportaIgualEnTodaLaJerarquia() {
        // 4.0 * 2.0 (UHD_4K)
        assertEquals(8.0, catalogo.get(0).calcularCosto(), 0.0001);
        // 2.0 * 1.5 (HD)
        assertEquals(3.0, catalogo.get(1).calcularCosto(), 0.0001);
        // 3.0 * 2.0 (UHD_4K)
        assertEquals(6.0, catalogo.get(2).calcularCosto(), 0.0001);
        // 1.0 * 1.0 (SD)
        assertEquals(1.0, catalogo.get(3).calcularCosto(), 0.0001);
    }

    /**
     * Dos tipos distintos con el mismo costo base y la misma calidad cuestan lo
     * mismo: la formula no depende de la subclase (sustitucion de Liskov).
     */
    @Test
    void elCostoNoDependeDelTipoConcretoSinoDelEstado() {
        Contenido pelicula = new Pelicula("x1", "A", Genero.ACCION, Calidad.HD, 100, 3.0, "Dir");
        Contenido podcast = new Podcast("x2", "B", Genero.ACCION, Calidad.HD, 100, 3.0, "Pres");

        assertEquals(pelicula.calcularCosto(), podcast.calcularCosto(), 0.0001);
    }

    /** La jerarquia cumple los dos contratos segregados del paquete contrato. */
    @Test
    void todoContenidoEsReproducibleYdetallable() {
        for (Contenido c : catalogo) {
            assertInstanceOf(streamflow.modelo.contrato.Reproducible.class, c);
            assertInstanceOf(streamflow.modelo.contrato.Detallable.class, c);
        }
    }
}
