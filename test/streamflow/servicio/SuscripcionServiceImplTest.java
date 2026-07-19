package streamflow.servicio;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import streamflow.modelo.Calidad;
import streamflow.modelo.Contenido;
import streamflow.modelo.Genero;
import streamflow.modelo.Pelicula;
import streamflow.modelo.Suscripcion;
import streamflow.modelo.Usuario;
import streamflow.persistencia.ContenidoDaoMemoria;

/**
 * Pruebas de {@link SuscripcionServiceImpl#calcularCostoMensual(Usuario)}: costo
 * base de la suscripcion mas el costo de los favoritos (costoBase x factor de
 * calidad). Aqui se verifica el factor del enum {@link Calidad}: SD=1.0, HD=1.5,
 * UHD_4K=2.0.
 */
class SuscripcionServiceImplTest {

    private ContenidoDaoMemoria contenidoDao;
    private SuscripcionServiceImpl service;

    private Contenido pelicula(String id, Calidad calidad, double costoBase) {
        return new Pelicula(id, "Peli " + id, Genero.ACCION, calidad, 120, costoBase, "Dir");
    }

    private Usuario usuarioConSuscripcion(double costoMensual) {
        Suscripcion s = new Suscripcion("s1", LocalDate.now(), costoMensual);
        return new Usuario("u1", "Pablo", "pablo@mail.com", s);
    }

    @BeforeEach
    void inicializar() {
        contenidoDao = new ContenidoDaoMemoria();
        service = new SuscripcionServiceImpl(contenidoDao);
    }

    @Test
    void usuarioNullDevuelveCero() {
        assertEquals(0.0, service.calcularCostoMensual(null), 0.0001);
    }

    @Test
    void sinFavoritosDevuelveSoloElCostoDeLaSuscripcion() {
        Usuario u = usuarioConSuscripcion(10.0);
        assertEquals(10.0, service.calcularCostoMensual(u), 0.0001);
    }

    @Test
    void aplicaElFactorDeCalidadDeLosFavoritos() {
        Usuario u = usuarioConSuscripcion(10.0);
        // costoBase 5.0 x factor HD 1.5 = 7.5  ->  10.0 + 7.5 = 17.5
        Contenido favHd = pelicula("c1", Calidad.HD, 5.0);
        contenidoDao.insertar(favHd);
        u.agregarFavorito(favHd);
        assertEquals(17.5, service.calcularCostoMensual(u), 0.0001);
    }

    @Test
    void sumaVariosFavoritosConDistintaCalidad() {
        Usuario u = usuarioConSuscripcion(0.0);
        Contenido sd = pelicula("c1", Calidad.SD, 4.0);      // 4.0 x 1.0 = 4.0
        Contenido uhd = pelicula("c2", Calidad.UHD_4K, 4.0); // 4.0 x 2.0 = 8.0
        contenidoDao.insertar(sd);
        contenidoDao.insertar(uhd);
        u.agregarFavorito(sd);
        u.agregarFavorito(uhd);
        assertEquals(12.0, service.calcularCostoMensual(u), 0.0001);
    }

    @Test
    void usaElPrecioActualDelDaoNoElDelFavoritoEnMemoria() {
        Usuario u = usuarioConSuscripcion(0.0);
        Contenido favoritoViejo = pelicula("c1", Calidad.SD, 5.0); // el usuario tiene 5.0
        // En la fuente de datos el precio subio a 9.0 (mismo id):
        contenidoDao.insertar(pelicula("c1", Calidad.SD, 9.0));
        u.agregarFavorito(favoritoViejo);
        // Debe tomar el 9.0 del DAO, no el 5.0 del favorito en memoria.
        assertEquals(9.0, service.calcularCostoMensual(u), 0.0001);
    }
}
