package streamflow.vista;

import java.util.List;
import streamflow.modelo.Contenido;

/**
 * Implementacion de {@link IVista} basada en la consola de texto.
 */
public class ConsolaVista implements IVista {

    @Override
    public void mostrarMenu() {
        System.out.println();
        System.out.println("========= STREAMFLOW =========");
        System.out.println("1. Listar contenidos");
        System.out.println("2. Facturar usuario");
        System.out.println("3. Recomendar por genero");
        System.out.println("0. Salir");
        System.out.println("==============================");
        System.out.print("Seleccione una opcion: ");
    }

    @Override
    public void mostrarContenidos(List<Contenido> lista) {
        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay contenidos para mostrar.");
            return;
        }
        System.out.println("--- Contenidos disponibles (" + lista.size() + ") ---");
        for (Contenido c : lista) {
            System.out.println(" - " + c.obtenerDetalles());
        }
    }

    /**
     * Muestra un mensaje generico en la consola.
     *
     * @param mensaje texto a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
