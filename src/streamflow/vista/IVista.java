package streamflow.vista;

import java.util.List;
import streamflow.modelo.Contenido;

public interface IVista {

    void mostrarMenu();

    void mostrarContenidos(List<Contenido> lista);

    /**
     * Arranca el bucle de interaccion con el usuario. Toda la lectura de
     * teclado (Scanner) y el menu viven en la vista; Main solo la invoca.
     * Los controladores que necesite la vista se inyectan en su constructor,
     * no en el contrato: usar controladores es un detalle de cada vista
     * concreta, no parte del rol "ser una vista".
     */
    void iniciar();
}
